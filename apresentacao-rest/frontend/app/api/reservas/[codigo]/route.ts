import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para cancelar reserva.
 * Faz proxy para o backend Spring Boot passando os cookies de sessão.
 */
export async function DELETE(
  request: NextRequest,
  context: { params: Promise<{ codigo: string }> }
) {
  try {
    // Next.js 15+ requer acesso assíncrono aos params
    const params = await context.params;
    const codigoReserva = params.codigo;

    console.log(
      "DELETE /api/reservas/[codigo] chamado com código:",
      codigoReserva
    );

    // Obter cookie de sessão do request
    const sessionCookie = request.cookies.get("JSESSIONID");

    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      return NextResponse.json({ error: "Não autenticado" }, { status: 401 });
    }

    // Fazer requisição para o backend Spring Boot
    const backendUrl = `http://localhost:8080/api/v1/reservas/${codigoReserva}`;
    const response = await fetch(backendUrl, {
      method: "DELETE",
      headers: {
        Cookie: cookieHeader,
      },
      credentials: "include",
    });

    // Se o backend retornar erro de autenticação, repassar
    if (response.status === 401 || response.status === 403) {
      const errorData = await response.json().catch(() => ({
        error: "Não autorizado",
      }));
      return NextResponse.json(errorData, { status: response.status });
    }

    // Se não for sucesso, tentar parsear JSON ou retornar erro genérico
    if (!response.ok) {
      // Verificar se a resposta é HTML (erro do Spring Boot)
      const contentType = response.headers.get("content-type");
      const responseText = await response.text();

      if (contentType && contentType.includes("text/html")) {
        console.error(
          "Backend retornou HTML em vez de JSON:",
          responseText.substring(0, 200)
        );
        return NextResponse.json(
          {
            error: "Erro ao cancelar reserva",
            message: `Erro ${response.status}: ${response.statusText}`,
            details:
              "O servidor retornou uma resposta HTML. Verifique os logs do backend.",
          },
          { status: response.status }
        );
      }

      // Tentar parsear como JSON
      try {
        const errorData = JSON.parse(responseText);
        return NextResponse.json(errorData, { status: response.status });
      } catch (parseError) {
        console.error("Erro ao parsear resposta do backend:", parseError);
        return NextResponse.json(
          {
            error: "Erro ao cancelar reserva",
            message: `Erro ${response.status}: ${response.statusText}`,
            details: responseText.substring(0, 200),
          },
          { status: response.status }
        );
      }
    }

    // Verificar se a resposta é JSON antes de parsear
    const contentType = response.headers.get("content-type");
    const responseText = await response.text();

    if (!contentType || !contentType.includes("application/json")) {
      console.error(
        "Backend retornou resposta não-JSON:",
        contentType,
        responseText.substring(0, 200)
      );
      return NextResponse.json(
        {
          error: "Resposta inválida do servidor",
          message: "O servidor retornou uma resposta que não é JSON",
        },
        { status: 500 }
      );
    }

    const data = JSON.parse(responseText);

    return NextResponse.json(data, {
      status: response.status,
    });
  } catch (error: any) {
    console.error("Erro ao cancelar reserva:", error);
    return NextResponse.json(
      { error: "Erro ao cancelar reserva", message: error.message },
      { status: 500 }
    );
  }
}
