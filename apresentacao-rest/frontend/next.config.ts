/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      // IMPORTANTE: Não fazer rewrite de /api/reservas/* pois temos API Routes do Next.js
      // Apenas fazer rewrite de rotas que não têm API Routes correspondentes
      // As rotas /api/reservas, /api/reservas/minhas e /api/reservas/[codigo] são tratadas pelas API Routes
    ];
  },
};

export default nextConfig;
