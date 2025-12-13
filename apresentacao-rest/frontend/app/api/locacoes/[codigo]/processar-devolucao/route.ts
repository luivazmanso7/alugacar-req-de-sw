import { NextRequest, NextResponse } from "next/server";

export async function POST(
  request: NextRequest,
  { params }: { params: { codigo: string } }
) {
  try {
    const codigoLocacao = params.codigo;
    const body = await request.json();

    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeaderFromRequest = request.headers.get("cookie");

    let cookieHeader = "";

    if (sessionCookie) {
      cookieHeader = `JSESSIONID=${sessionCookie.value}`;
    } else if (cookieHeaderFromRequest) {
      const jsessionMatch = cookieHeaderFromRequest.match(/JSESSIONID=([^;]+)/);
      if (jsessionMatch) {
        cookieHeader = `JSESSIONID=${jsessionMatch[1]}`;
      }
    }

    if (!cookieHeader) {
      console.warn(
        "JSESSIONID não encontrado no request para processar devolução."
      );
      return NextResponse.json(
        { error: "Sessão não encontrada. Faça login primeiro." },
        { status: 401 }
      );
    }

    const backendUrl = `http://localhost:8080/api/v1/admin/locacoes/${codigoLocacao}/processar-devolucao`;
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
            "Erro ao processar devolução",
        },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (error: any) {
    console.error("Erro ao processar devolução:", error);
    return NextResponse.json(
      { error: "Erro ao processar devolução", message: error.message },
      { status: 500 }
    );
  }
}
