import { NextRequest, NextResponse } from "next/server";

export async function POST(
  request: NextRequest,
  { params }: { params: { placa: string } }
) {
  try {
    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      return NextResponse.json({ error: "Não autenticado" }, { status: 401 });
    }

    const body = await request.json();
    const placa = params.placa;

    const backendUrl = `http://localhost:8080/api/v1/admin/veiculos/${placa}/agendar-manutencao`;
    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        Cookie: cookieHeader,
        "Content-Type": "application/json",
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
            "Erro ao agendar manutenção",
        },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true }, { status: response.status });
  } catch (error: any) {
    console.error("Erro ao agendar manutenção:", error);
    return NextResponse.json(
      {
        error: "Erro ao agendar manutenção",
        message: error.message,
      },
      { status: 500 }
    );
  }
}
