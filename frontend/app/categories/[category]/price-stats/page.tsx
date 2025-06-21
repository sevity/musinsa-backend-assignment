// frontend/app/categories/price-stats/page.tsx
"use client";

import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";
import { useParams } from "next/navigation";

type BrandPrice = { brand: string; price: number };
type Resp = {
  category: string;
  lowest: BrandPrice[];
  highest: BrandPrice[];
};

export default function PriceStats() {
  // ① URL 파라미터 디코드 → 안전 인코드
  const { category } = useParams<{ category: string }>();
  const decoded = decodeURIComponent(category || "");
  const safePath = encodeURIComponent(decoded);

  // ② API 호출 (항상 최신 데이터만 가져오도록 옵션 추가)
  const { data, isLoading, error } = useQuery<Resp, Error>({
    queryKey: ["priceStats", decoded],
    queryFn: () =>
      api.get(`/categories/${safePath}/price-stats`).then((r) => r.data),
    // 페이지 마운트·포커스·네트워크 복구 시마다 재요청
    refetchOnMount: "always",
    refetchOnWindowFocus: "always",
    refetchOnReconnect: "always",
    // 캐시 무효화
    staleTime: 0,
    retry: 1,
  });

  if (isLoading) return <p>Loading…</p>;
  if (error)
    return (
      <p className="text-red-600">
        에러: {(error as any).message}
      </p>
    );

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold">
        {data!.category} – 최저 &amp; 최고
      </h2>

      <table className="w-full text-sm border-collapse">
        <thead>
          <tr className="bg-gray-100">
            <th className="p-2 border">종류</th>
            <th className="p-2 border">브랜드</th>
            <th className="p-2 border text-right">가격(원)</th>
          </tr>
        </thead>
        <tbody>
          {[
            { label: "최저가", list: data!.lowest },
            { label: "최고가", list: data!.highest },
          ].map(({ label, list }) =>
            list.map((item) => (
              <tr key={`${label}-${item.brand}`}>
                <td className="p-2 border">{label}</td>
                <td className="p-2 border">{item.brand}</td>
                <td className="p-2 border text-right">
                  {item.price.toLocaleString()} 원
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
