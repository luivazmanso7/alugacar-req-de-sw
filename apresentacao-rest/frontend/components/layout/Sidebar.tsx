'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  LayoutDashboard,
  Search,
  Key,
  RotateCcw,
  Car,
  Settings,
  HelpCircle,
  ChevronLeft,
  Menu,
} from 'lucide-react';

interface SidebarProps {
  children: React.ReactNode;
}

export default function Sidebar({ children }: SidebarProps) {
  const [collapsed, setCollapsed] = useState(false);
  const pathname = usePathname();

  const menuItems = [
    {
      section: 'PRINCIPAL',
      items: [
        {
          name: 'Dashboard',
          icon: LayoutDashboard,
          href: '/dashboard',
        },
        {
          name: 'Buscar Reserva',
          icon: Search,
          href: '/reservas/buscar',
        },
        {
          name: 'Retirada',
          icon: Key,
          href: '/retirada',
        },
        {
          name: 'Devolução',
          icon: RotateCcw,
          href: '/devolucao',
        },
      ],
    },
    {
      section: 'GERENCIAMENTO',
      items: [
        {
          name: 'Frota',
          icon: Car,
          href: '/frota',
        },
        {
          name: 'Configurações',
          icon: Settings,
          href: '/configuracoes',
        },
        {
          name: 'Ajuda',
          icon: HelpCircle,
          href: '/ajuda',
        },
      ],
    },
  ];

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <aside
        className={`${
          collapsed ? 'w-20' : 'w-64'
        } bg-slate-800 text-white transition-all duration-300 flex flex-col`}
      >
        {/* Logo */}
        <div className="flex items-center justify-between p-6 border-b border-slate-700">
          {!collapsed && (
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 bg-cyan-600 rounded-lg flex items-center justify-center">
                <Car className="w-5 h-5" />
              </div>
              <span className="text-xl font-semibold">RentaCar</span>
            </div>
          )}
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="p-2 hover:bg-slate-700 rounded-lg transition-colors"
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
                <div className="px-6 mb-3 text-xs font-semibold text-slate-400 uppercase tracking-wider">
                  {section.section}
                </div>
              )}
              <div className="space-y-1 px-3">
                {section.items.map((item, itemIndex) => {
                  const isActive = pathname === item.href;
                  return (
                    <Link
                      key={itemIndex}
                      href={item.href}
                      className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors ${
                        isActive
                          ? 'bg-cyan-600 text-white'
                          : 'text-slate-300 hover:bg-slate-700 hover:text-white'
                      }`}
                      title={collapsed ? item.name : ''}
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
        <div className="border-t border-slate-700 p-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-cyan-600 rounded-full flex items-center justify-center flex-shrink-0">
              <span className="text-sm font-semibold">C</span>
            </div>
            {!collapsed && (
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate">Carlos</p>
                <p className="text-xs text-slate-400 truncate">Atendente</p>
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
