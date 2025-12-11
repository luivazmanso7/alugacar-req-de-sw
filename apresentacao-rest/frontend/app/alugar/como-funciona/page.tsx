"use client";

export default function ComoFuncionaPage() {
  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Como Funciona</h1>
      <div className="bg-white rounded-lg shadow-md p-8">
        <div className="space-y-6">
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              1. Busque seu veículo
            </h2>
            <p className="text-gray-600">
              Informe a cidade onde deseja retirar o veículo e selecione as
              datas de retirada e devolução.
            </p>
          </div>
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              2. Escolha o veículo ideal
            </h2>
            <p className="text-gray-600">
              Veja os veículos disponíveis e escolha o que melhor atende suas
              necessidades.
            </p>
          </div>
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              3. Faça sua reserva
            </h2>
            <p className="text-gray-600">
              Preencha seus dados e confirme a reserva. Você receberá um código
              de confirmação.
            </p>
          </div>
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              4. Retire o veículo
            </h2>
            <p className="text-gray-600">
              No dia agendado, compareça ao local de retirada com seus
              documentos e o código da reserva.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
