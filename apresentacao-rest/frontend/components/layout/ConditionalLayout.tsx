"use client";

import { usePathname } from "next/navigation";
import Sidebar from "./Sidebar";

interface ConditionalLayoutProps {
  children: React.ReactNode;
}

export default function ConditionalLayout({
  children,
}: ConditionalLayoutProps) {
  const pathname = usePathname();

  // Se a rota começar com /alugar, não aplicar a sidebar de gerentes
  // (a sidebar de clientes será aplicada pelo layout específico)
  if (pathname?.startsWith("/alugar")) {
    return <>{children}</>;
  }

  // Para outras rotas, aplicar a sidebar de gerentes
  return <Sidebar>{children}</Sidebar>;
}
