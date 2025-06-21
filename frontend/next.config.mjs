/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,

  // 모든 /api/v1/* 요청을 백엔드(8080)로 프록시
  async rewrites() {
    return [
      {
        source: '/api/v1/:path*',
        destination: 'http://localhost:8080/api/v1/:path*',
      },
    ];
  },
};

export default nextConfig;
