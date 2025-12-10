// Tipos e interfaces para o sistema de locação

export interface Cliente {
  id?: string;
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
  cnh: string;
  endereco?: Endereco;
}

export interface Endereco {
  rua: string;
  numero: string;
  complemento?: string;
  bairro: string;
  cidade: string;
  estado: string;
  cep: string;
}

export interface Veiculo {
  id: string;
  placa: string;
  modelo: string;
  marca: string;
  ano: number;
  cor: string;
  categoria: CategoriaVeiculo;
  status: StatusVeiculo;
  quilometragem: number;
}

export enum CategoriaVeiculo {
  ECONOMICO = 'Econômico',
  EXECUTIVO = 'Executivo',
  SUV = 'SUV',
  LUXO = 'Luxo',
}

export enum StatusVeiculo {
  DISPONIVEL = 'Disponível',
  ALUGADO = 'Alugado',
  MANUTENCAO = 'Manutenção',
  RESERVADO = 'Reservado',
}

export interface Reserva {
  id?: string;
  codigo: string;
  cliente: Cliente;
  veiculo?: Veiculo;
  categoria: CategoriaVeiculo;
  dataRetirada: string;
  dataDevolucao: string;
  status: StatusReserva;
  valorEstimado?: number;
  observacoes?: string;
}

export enum StatusReserva {
  PENDENTE = 'Pendente',
  CONFIRMADA = 'Confirmada',
  EM_ANDAMENTO = 'Em Andamento',
  CONCLUIDA = 'Concluída',
  CANCELADA = 'Cancelada',
}

export interface Contrato {
  id?: string;
  numero: string;
  reserva: Reserva;
  veiculo: Veiculo;
  dataRetirada: string;
  dataDevolucaoPrevista: string;
  quilometragemInicial: number;
  combustivelInicial: number;
  valorDiaria: number;
  valorTotal: number;
  assinaturaCliente?: string;
  assinaturaAtendente?: string;
}

export interface Devolucao {
  id?: string;
  contrato: Contrato;
  dataDevolucao: string;
  quilometragemFinal: number;
  combustivelFinal: number;
  estadoVeiculo: EstadoVeiculo;
  observacoes?: string;
  valorMultas?: number;
  valorAdicional?: number;
  valorTotal: number;
}

export enum EstadoVeiculo {
  EXCELENTE = 'Excelente',
  BOM = 'Bom',
  REGULAR = 'Regular',
  DANIFICADO = 'Danificado',
}

export interface Manutencao {
  id?: string;
  veiculo: Veiculo;
  tipo: TipoManutencao;
  descricao: string;
  dataAgendada: string;
  dataRealizacao?: string;
  custo?: number;
  status: StatusManutencao;
}

export enum TipoManutencao {
  PREVENTIVA = 'Preventiva',
  CORRETIVA = 'Corretiva',
  REVISAO = 'Revisão',
}

export enum StatusManutencao {
  AGENDADA = 'Agendada',
  EM_ANDAMENTO = 'Em Andamento',
  CONCLUIDA = 'Concluída',
  CANCELADA = 'Cancelada',
}

export interface DashboardStats {
  reservasDoDia: number;
  reservasDoDiaChange?: string;
  retiradasPendentes: number;
  devolucoesPendentes: number;
  veiculosAlugados: number;
  veiculosDisponiveis: number;
  veiculosManutencao: number;
}
