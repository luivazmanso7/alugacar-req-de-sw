import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para login.
 * Faz proxy para o backend Spring Boot e repassa o cookie de sessão.
 */
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    // Fazer requisição para o backend Spring Boot
    const backendUrl = "http://localhost:8080/api/v1/auth/login";
    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    // Verificar se o login foi bem-sucedido
    if (!response.ok) {
      const errorData = await response.json();
      return NextResponse.json(errorData, { status: response.status });
    }

    const data = await response.json();

    // Criar resposta Next.js
    const nextResponse = NextResponse.json(data, {
      status: response.status,
    });

    // Repassar cookie de sessão do backend para o cliente
    const setCookieHeader = response.headers.get("Set-Cookie");
    console.log("Set-Cookie header do backend:", setCookieHeader);

    if (setCookieHeader) {
      // Extrair apenas o valor do cookie JSESSIONID e seus atributos
      const jsessionIdMatch = setCookieHeader.match(/JSESSIONID=([^;]+)/);
      if (jsessionIdMatch) {
        const jsessionId = jsessionIdMatch[1];
        console.log("JSESSIONID extraído:", jsessionId);

        // Extrair path do cookie original (se houver)
        const pathMatch = setCookieHeader.match(/Path=([^;]+)/);
        const cookiePath = pathMatch ? pathMatch[1] : "/";

        // Configurar cookie para o domínio localhost:3000
        // IMPORTANTE: O Spring Boot está configurado com context-path=/api/v1
        // E o cookie do Spring Boot tem path=/api/v1
        // Mas como estamos fazendo proxy através do Next.js, precisamos usar path="/"
        // para que o cookie seja enviado em todas as requisições
        nextResponse.cookies.set("JSESSIONID", jsessionId, {
          httpOnly: true,
          secure: false,
          sameSite: "lax",
          path: "/", // Usar "/" para funcionar com o proxy do Next.js
          maxAge: 60 * 60 * 24 * 7, // 7 dias
        });

        console.log("Cookie JSESSIONID configurado no Next.js com path=/");

        console.log(
          "Cookie JSESSIONID configurado no Next.js:",
          jsessionId.substring(0, 20) + "..."
        );
      } else {
        console.warn(
          "Não foi possível extrair JSESSIONID do Set-Cookie header"
        );
      }
    } else {
      console.warn("Backend não retornou Set-Cookie header");
    }

    return nextResponse;
  } catch (error: any) {
    console.error("Erro ao fazer login:", error);
    return NextResponse.json(
      { error: "Erro ao fazer login", message: error.message },
      { status: 500 }
    );
  }
}
