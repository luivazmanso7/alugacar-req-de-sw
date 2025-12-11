"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Car,
  Calendar,
  User,
  HelpCircle,
  Phone,
  ChevronLeft,
  Menu,
  Home,
} from "lucide-react";

interface ClienteSidebarProps {
  children: React.ReactNode;
}

export default function ClienteSidebar({ children }: ClienteSidebarProps) {
  const [collapsed, setCollapsed] = useState(false);
  const pathname = usePathname();

  const menuItems = [
    {
      section: "NAVEGAÇÃO",
      items: [
        {
          name: "Alugar Carro",
          icon: Home,
          href: "/alugar",
        },
        {
          name: "Minhas Reservas",
          icon: Calendar,
          href: "/alugar/minhas-reservas",
        },
        {
          name: "Meu Perfil",
          icon: User,
          href: "/alugar/perfil",
        },
      ],
    },
    {
      section: "AJUDA",
      items: [
        {
          name: "Como Funciona",
          icon: HelpCircle,
          href: "/alugar/como-funciona",
        },
        {
          name: "Contato",
          icon: Phone,
          href: "/alugar/contato",
        },
      ],
    },
  ];

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <aside
        className={`${
          collapsed ? "w-20" : "w-64"
        } bg-blue-900 text-white transition-all duration-300 flex flex-col`}
      >
        {/* Logo */}
        <div className="flex items-center justify-between p-6 border-b border-blue-800">
          {!collapsed && (
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 bg-white rounded-lg flex items-center justify-center">
                <Car className="w-5 h-5 text-blue-900" />
              </div>
              <span className="text-xl font-bold">alucar</span>
            </div>
          )}
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="p-2 hover:bg-blue-800 rounded-lg transition-colors"
          >
            {collapsed ? (
              <Menu className="w-5 h-5" />
            ) : (
              <ChevronLeft className="w-5 h-5" />
            )}
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto py-6">
          {menuItems.map((section, sectionIndex) => (
            <div key={sectionIndex} className="mb-6">
              {!collapsed && (
                <div className="px-6 mb-3 text-xs font-semibold text-blue-300 uppercase tracking-wider">
                  {section.section}
                </div>
              )}
              <div className="space-y-1 px-3">
                {section.items.map((item, itemIndex) => {
                  const isActive =
                    pathname === item.href ||
                    (item.href === "/alugar" &&
                      pathname?.startsWith("/alugar"));
                  return (
                    <Link
                      key={itemIndex}
                      href={item.href}
                      className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors ${
                        isActive
                          ? "bg-white text-blue-900 font-semibold"
                          : "text-blue-100 hover:bg-blue-800 hover:text-white"
                      }`}
                      title={collapsed ? item.name : ""}
                    >
                      <item.icon className="w-5 h-5 flex-shrink-0" />
                      {!collapsed && (
                        <span className="text-sm font-medium">{item.name}</span>
                      )}
                    </Link>
                  );
                })}
              </div>
            </div>
          ))}
        </nav>

        {/* User Profile */}
        <div className="border-t border-blue-800 p-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center flex-shrink-0">
              <User className="w-5 h-5 text-blue-900" />
            </div>
            {!collapsed && (
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate">Cliente</p>
                <p className="text-xs text-blue-300 truncate">
                  Área do Cliente
                </p>
              </div>
            )}
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1 overflow-auto">{children}</div>
    </div>
  );
}
