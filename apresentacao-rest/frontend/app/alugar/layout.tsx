import type { Metadata } from "next";
import ClienteSidebar from "@/components/layout/ClienteSidebar";

export const metadata: Metadata = {
  title: "Alugar Carro - alucar",
  description: "Alugue o carro perfeito de forma rápida e fácil",
};

export default function AlugarLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return <ClienteSidebar>{children}</ClienteSidebar>;
}
