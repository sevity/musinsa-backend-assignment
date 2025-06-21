/* frontend/app/layout.tsx */

import "./globals.css";
import type { ReactNode } from "react";
import Link from "next/link";
import Providers from "@/components/Providers";   // ✅ 추가

export const metadata = { title: "MUSINSA Backend Assignment API UI" };

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="ko">
      <body className="min-h-screen bg-gray-50">
        {/* ❇️  모든 Client-side context(React Query 등)는 Providers 내부에서만 */}
        <Providers>
          <nav className="px-6 py-3 bg-black text-white flex flex-wrap gap-4">
            <Link href="/" className="font-bold">
              🏠 홈
            </Link>
            <Link href="/categories/cheapest-brands">카테고리 최저가</Link>
            <Link href="/categories/price-stats">카테고리 최저·최고</Link>
            <Link href="/brands/cheapest">브랜드 최저가</Link>
            <Link href="/admin">관리(Admin)</Link>
          </nav>

          <main className="p-6 max-w-4xl mx-auto">{children}</main>
        </Providers>
      </body>
    </html>
  );
}
