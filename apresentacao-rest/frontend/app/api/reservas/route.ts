import { NextRequest, NextResponse } from "next/server";

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      return NextResponse.json(
        {
          error: "Não autenticado",
          message: "É necessário fazer login para criar uma reserva",
        },
        { status: 401 }
      );
    }

    const backendUrl = "http://localhost:8080/api/v1/reservas";
    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(cookieHeader && { Cookie: cookieHeader }),
      },
      body: JSON.stringify(body),
    });

    const responseText = await response.text();

    if (!response.ok) {
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
      return NextResponse.json(errorData, { status: response.status });
    }

    let data;
    try {
      data = responseText ? JSON.parse(responseText) : {};
    } catch (error) {
      return NextResponse.json(
        {
          error: "Erro ao processar resposta do servidor",
          details: responseText.substring(0, 200),
        },
        { status: 500 }
      );
    }

    const nextResponse = NextResponse.json(data, {
      status: response.status,
    });

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
    return NextResponse.json(
      { error: "Erro ao criar reserva", message: error.message },
      { status: 500 }
    );
  }
}
