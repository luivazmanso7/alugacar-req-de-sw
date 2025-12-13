import { NextRequest, NextResponse } from "next/server";

export async function POST(
  request: NextRequest,
  { params }: { params: { codigo: string } }
) {
  try {
    const codigoReserva = params.codigo;
    const body = await request.json();

    // Obter cookie de sessão do request
    // Tentar obter de diferentes formas para garantir compatibilidade
    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeaderFromRequest = request.headers.get("cookie");

    let cookieHeader = "";

    if (sessionCookie) {
      cookieHeader = `JSESSIONID=${sessionCookie.value}`;
    } else if (cookieHeaderFromRequest) {
      // Extrair JSESSIONID do header Cookie se existir
      const jsessionMatch = cookieHeaderFromRequest.match(/JSESSIONID=([^;]+)/);
      if (jsessionMatch) {
        cookieHeader = `JSESSIONID=${jsessionMatch[1]}`;
      }
    }

    if (!cookieHeader) {
      return NextResponse.json(
        { error: "Sessão não encontrada. Faça login primeiro." },
        { status: 401 }
      );
    }

    const backendUrl = `http://localhost:8080/api/v1/admin/reservas/${codigoReserva}/confirmar-retirada`;
    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Cookie: cookieHeader,
      },
      credentials: "include",
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          error:
            errorData.message ||
            errorData.error ||
            "Erro ao confirmar retirada",
        },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true }, { status: response.status });
  } catch (error: any) {
    console.error("Erro ao confirmar retirada:", error);
    return NextResponse.json(
      { error: "Erro ao confirmar retirada", message: error.message },
      { status: 500 }
    );
  }
}
