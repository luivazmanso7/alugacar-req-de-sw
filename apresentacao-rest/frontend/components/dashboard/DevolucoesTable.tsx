import React from "react";
import Link from "next/link";

interface Devolucao {
  codigo: string;
  cliente: {
    nome: string;
    telefone: string;
  };
  periodo: string;
  categoria: string;
}

interface DevolucoesTableProps {
  title: string;
  data: Devolucao[];
  linkText: string;
  linkHref: string;
}

export const DevolucoesTable: React.FC<DevolucoesTableProps> = ({
  title,
  data,
  linkText,
  linkHref,
}) => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100">
      <div className="p-6 border-b border-gray-100 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
        <Link
          href={linkHref}
          className="text-sm text-cyan-600 hover:text-cyan-700 font-medium"
        >
          {linkText}
        </Link>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Código
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Cliente
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Período
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Categoria
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-100">
            {data.map((devolucao, index) => (
              <tr
                key={index}
                className="hover:bg-gray-50 transition-colors cursor-pointer"
              >
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {devolucao.codigo}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      {devolucao.cliente.nome}
                    </div>
                    {devolucao.cliente.telefone && (
                      <div className="text-sm text-gray-500">
                        {devolucao.cliente.telefone}
                      </div>
                    )}
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {devolucao.periodo}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {devolucao.categoria}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
