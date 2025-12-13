import { NextRequest, NextResponse } from "next/server";

export async function GET(request: NextRequest) {
  try {
    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      return NextResponse.json({ error: "Não autenticado" }, { status: 401 });
    }

    const backendUrl = "http://localhost:8080/api/v1/reservas/minhas";
    const response = await fetch(backendUrl, {
      method: "GET",
      headers: {
        Cookie: cookieHeader,
      },
      credentials: "include",
    });

    if (response.status === 401 || response.status === 403) {
      return NextResponse.json(
        { error: "Não autenticado" },
        { status: response.status }
      );
    }

    if (!response.ok) {
      try {
        const errorData = await response.json();
        return NextResponse.json(errorData, { status: response.status });
      } catch {
        return NextResponse.json(
          { error: "Erro ao listar reservas" },
          { status: response.status }
        );
      }
    }

    const data = await response.json();
    return NextResponse.json(data, {
      status: response.status,
    });
  } catch (error: any) {
    return NextResponse.json(
      { error: "Erro ao listar reservas", message: error.message },
      { status: 500 }
    );
  }
}
