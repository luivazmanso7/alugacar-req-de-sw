"use client";

import { Phone, Mail, MapPin } from "lucide-react";

export default function ContatoPage() {
  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Contato</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mr-4">
              <Phone className="w-6 h-6 text-blue-600" />
            </div>
            <h2 className="text-xl font-semibold text-gray-900">Telefone</h2>
          </div>
          <p className="text-gray-600">(11) 3000-0000</p>
          <p className="text-gray-600">(11) 99999-9999</p>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mr-4">
              <Mail className="w-6 h-6 text-blue-600" />
            </div>
            <h2 className="text-xl font-semibold text-gray-900">E-mail</h2>
          </div>
          <p className="text-gray-600">contato@alucar.com.br</p>
          <p className="text-gray-600">suporte@alucar.com.br</p>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mr-4">
              <MapPin className="w-6 h-6 text-blue-600" />
            </div>
            <h2 className="text-xl font-semibold text-gray-900">Endereço</h2>
          </div>
          <p className="text-gray-600">Rua Exemplo, 123</p>
          <p className="text-gray-600">São Paulo - SP</p>
          <p className="text-gray-600">CEP: 01234-567</p>
        </div>
      </div>
    </div>
  );
}
