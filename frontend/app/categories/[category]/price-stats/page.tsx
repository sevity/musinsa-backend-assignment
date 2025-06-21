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
  // ① 파라미터 디코드 → 안전 인코드
  const { category } = useParams<{ category: string }>();
  const decoded = decodeURIComponent(category || "");
  const safePath = encodeURIComponent(decoded);

  // ② API 호출
  const { data, isLoading, error } = useQuery<Resp>({
    queryKey: ["priceStats", decoded],
    queryFn: () =>
      api.get(`/categories/${safePath}/price-stats`).then((r) => r.data),
  });

  if (isLoading) return <p>Loading…</p>;

  if (error) {
    // 에러 파싱 로직 (생략)
    return <p className="text-red-600">에러: {(error as any).message}</p>;
  }

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
