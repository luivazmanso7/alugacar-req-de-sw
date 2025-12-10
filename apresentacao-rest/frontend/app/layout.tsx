import type { Metadata } from "next";
import "./globals.css";
import Sidebar from "@/components/layout/Sidebar";

export const metadata: Metadata = {
  title: "RentaCar - Sistema de Locação de Veículos",
  description: "Sistema de gerenciamento de locação de veículos",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body className="antialiased">
        <Sidebar>{children}</Sidebar>
      </body>
    </html>
  );
}
