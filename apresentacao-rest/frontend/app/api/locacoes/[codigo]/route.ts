import { NextRequest, NextResponse } from "next/server";

export async function GET(
  request: NextRequest,
  context: { params: { codigo: string } }
) {
  try {
    const codigoLocacao = context.params.codigo;

    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      return NextResponse.json({ error: "Não autenticado" }, { status: 401 });
    }

    const backendUrl = `http://localhost:8080/api/v1/locacoes/${codigoLocacao}`;
    const response = await fetch(backendUrl, {
      method: "GET",
      headers: {
        Cookie: cookieHeader,
        "Content-Type": "application/json",
      },
      credentials: "include",
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          error:
            errorData.message || errorData.error || "Locação não encontrada",
        },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (error: any) {
    console.error("Erro ao buscar locação:", error);
    return NextResponse.json(
      { error: "Erro ao buscar locação", message: error.message },
      { status: 500 }
    );
  }
}
