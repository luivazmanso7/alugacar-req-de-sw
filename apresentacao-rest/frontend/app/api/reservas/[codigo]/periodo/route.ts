import { NextRequest, NextResponse } from "next/server";

export async function PATCH(
  request: NextRequest,
  context: { params: Promise<{ codigo: string }> }
) {
  try {
    const params = await context.params;
    const codigoReserva = params.codigo;

    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      return NextResponse.json({ error: "Não autenticado" }, { status: 401 });
    }

    const body = await request.json();

    const backendUrl = `http://localhost:8080/api/v1/reservas/${codigoReserva}/periodo`;
    const response = await fetch(backendUrl, {
      method: "PATCH",
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
            "Erro ao alterar período da reserva",
        },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data, { status: response.status });
  } catch (error: any) {
    return NextResponse.json(
      {
        error: "Erro ao alterar período da reserva",
        message: error.message,
      },
      { status: 500 }
    );
  }
}
