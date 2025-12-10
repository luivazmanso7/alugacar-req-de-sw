/**
 * Formata um CPF para o padrão XXX.XXX.XXX-XX
 */
export function formatarCPF(cpf: string): string {
  const numeros = cpf.replace(/\D/g, "");
  return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

/**
 * Formata um telefone para o padrão (XX) XXXXX-XXXX
 */
export function formatarTelefone(telefone: string): string {
  const numeros = telefone.replace(/\D/g, "");
  if (numeros.length === 11) {
    return numeros.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
  }
  return numeros.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3");
}

/**
 * Formata um CEP para o padrão XXXXX-XXX
 */
export function formatarCEP(cep: string): string {
  const numeros = cep.replace(/\D/g, "");
  return numeros.replace(/(\d{5})(\d{3})/, "$1-$2");
}

/**
 * Formata uma data para o padrão DD/MM/YYYY
 */
export function formatarData(data: string | Date): string {
  const date = typeof data === "string" ? new Date(data) : data;
  return date.toLocaleDateString("pt-BR");
}

/**
 * Formata uma data e hora para o padrão DD/MM/YYYY HH:MM
 */
export function formatarDataHora(data: string | Date): string {
  const date = typeof data === "string" ? new Date(data) : data;
  return date.toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

/**
 * Formata um valor monetário para o padrão R$ X.XXX,XX
 */
export function formatarMoeda(valor: number): string {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(valor);
}

/**
 * Calcula o número de dias entre duas datas
 */
export function calcularDias(
  dataInicio: string | Date,
  dataFim: string | Date
): number {
  const inicio =
    typeof dataInicio === "string" ? new Date(dataInicio) : dataInicio;
  const fim = typeof dataFim === "string" ? new Date(dataFim) : dataFim;
  const diff = fim.getTime() - inicio.getTime();
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
}

/**
 * Valida um CPF
 */
export function validarCPF(cpf: string): boolean {
  const numeros = cpf.replace(/\D/g, "");

  if (numeros.length !== 11) return false;
  if (/^(\d)\1{10}$/.test(numeros)) return false;

  let soma = 0;
  let resto;

  for (let i = 1; i <= 9; i++) {
    soma += parseInt(numeros.substring(i - 1, i)) * (11 - i);
  }

  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(numeros.substring(9, 10))) return false;

  soma = 0;
  for (let i = 1; i <= 10; i++) {
    soma += parseInt(numeros.substring(i - 1, i)) * (12 - i);
  }

  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(numeros.substring(10, 11))) return false;

  return true;
}

/**
 * Valida um email
 */
export function validarEmail(email: string): boolean {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

/**
 * Gera um código de reserva único
 */
export function gerarCodigoReserva(): string {
  const ano = new Date().getFullYear();
  const random = Math.floor(Math.random() * 1000)
    .toString()
    .padStart(3, "0");
  return `RES-${ano}-${random}`;
}

/**
 * Converte uma string para título (primeira letra maiúscula)
 */
export function paraTitle(texto: string): string {
  return texto.charAt(0).toUpperCase() + texto.slice(1).toLowerCase();
}

/**
 * Trunca um texto com reticências
 */
export function truncar(texto: string, tamanho: number): string {
  if (texto.length <= tamanho) return texto;
  return texto.substring(0, tamanho) + "...";
}
