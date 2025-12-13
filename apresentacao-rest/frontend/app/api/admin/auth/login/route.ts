import { NextRequest, NextResponse } from "next/server";

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    const backendUrl = "http://localhost:8080/api/v1/admin/auth/login";
    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const errorData = await response.json();
      return NextResponse.json(errorData, { status: response.status });
    }

    const data = await response.json();

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
          path: "/",
          maxAge: 60 * 60 * 24 * 7,
        });
      }
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
