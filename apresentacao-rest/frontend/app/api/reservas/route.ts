import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para criar reserva.
 * Faz proxy para o backend Spring Boot passando os cookies de sessão.
 */
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    // Obter cookie de sessão do request
    const sessionCookie = request.cookies.get("JSESSIONID");

    // Log de todos os cookies recebidos para debug
    const allCookies = request.cookies.getAll();
    console.log(
      "Todos os cookies recebidos na criação de reserva:",
      allCookies.map((c) => c.name)
    );
    console.log(
      "Cookie JSESSIONID recebido na criação de reserva:",
      sessionCookie?.value
        ? `SIM (${sessionCookie.value.substring(0, 20)}...)`
        : "NÃO"
    );

    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      console.warn("Requisição de criação de reserva sem cookie JSESSIONID");
      return NextResponse.json(
        {
          error: "Não autenticado",
          message: "É necessário fazer login para criar uma reserva",
        },
        { status: 401 }
      );
    }

    // Fazer requisição para o backend Spring Boot
    const backendUrl = "http://localhost:8080/api/v1/reservas";
    console.log("Enviando requisição para:", backendUrl);
    console.log("Body:", JSON.stringify(body));

    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(cookieHeader && { Cookie: cookieHeader }),
      },
      body: JSON.stringify(body),
    });

    console.log("Status da resposta do backend:", response.status);
    console.log(
      "Headers da resposta:",
      Object.fromEntries(response.headers.entries())
    );

    // Ler o texto da resposta primeiro
    const responseText = await response.text();
    console.log("Resposta do backend (texto):", responseText.substring(0, 200));

    // Verificar se a resposta foi bem-sucedida
    if (!response.ok) {
      // Tentar parsear o erro como JSON, se não conseguir, retornar erro genérico
      let errorData;
      try {
        errorData = responseText
          ? JSON.parse(responseText)
          : { error: "Erro ao criar reserva" };
      } catch {
        errorData = {
          error: "Erro ao criar reserva",
          message: `Status: ${response.status}`,
          details: responseText.substring(0, 200),
        };
      }
      console.error("Erro do backend ao criar reserva:", errorData);
      return NextResponse.json(errorData, { status: response.status });
    }

    // Parsear resposta JSON apenas se houver conteúdo
    let data;
    try {
      data = responseText ? JSON.parse(responseText) : {};
    } catch (error) {
      console.error("Erro ao parsear resposta do backend:", error);
      console.error("Texto da resposta:", responseText);
      return NextResponse.json(
        {
          error: "Erro ao processar resposta do servidor",
          details: responseText.substring(0, 200),
        },
        { status: 500 }
      );
    }

    // Criar resposta Next.js
    const nextResponse = NextResponse.json(data, {
      status: response.status,
    });

    // Repassar cookie de sessão se houver atualização
    const setCookieHeader = response.headers.get("Set-Cookie");
    if (setCookieHeader) {
      const jsessionIdMatch = setCookieHeader.match(/JSESSIONID=([^;]+)/);
      if (jsessionIdMatch) {
        const jsessionId = jsessionIdMatch[1];
        const pathMatch = setCookieHeader.match(/Path=([^;]+)/);
        const cookiePath = pathMatch ? pathMatch[1] : "/";

        nextResponse.cookies.set("JSESSIONID", jsessionId, {
          httpOnly: true,
          secure: false,
          sameSite: "lax",
          path: cookiePath,
        });
      }
    }

    return nextResponse;
  } catch (error: any) {
    console.error("Erro ao criar reserva:", error);
    return NextResponse.json(
      { error: "Erro ao criar reserva", message: error.message },
      { status: 500 }
    );
  }
}
