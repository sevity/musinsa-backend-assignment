// frontend/app/categories/cheapest-brands/page.tsx
"use client";

import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";

type Item = { category: string; brand: string; price: number };
type Resp = { items: Item[]; total: number };

export default function CheapestByCategory() {
  const { data, isLoading, error } = useQuery<Resp, Error>({
    queryKey: ["cheapestByCat"],
    queryFn: () =>
      api.get("/categories/cheapest-brands").then((r) => r.data),
    // 페이지가 마운트될 때마다 항상 재요청
    refetchOnMount: "always",
    // 창 포커스 시 재요청
    refetchOnWindowFocus: "always",
    // 네트워크 복구 시 재요청
    refetchOnReconnect: "always",
  });

  if (isLoading) return <p>Loading…</p>;
  if (error)
    return (
      <p className="text-red-600">에러: {(error as any).message}</p>
    );

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold">카테고리별 최저가</h2>
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-gray-100">
            <th className="p-2 text-left">카테고리</th>
            <th className="p-2 text-left">브랜드</th>
            <th className="p-2 text-right">가격</th>
          </tr>
        </thead>
        <tbody>
          {data!.items.map((it) => (
            <tr key={it.category}>
              <td className="p-2">{it.category}</td>
              <td className="p-2">{it.brand}</td>
              <td className="p-2 text-right">
                {it.price.toLocaleString()}
              </td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr className="font-bold">
            <td className="p-2" colSpan={2}>
              총액
            </td>
            <td className="p-2 text-right">
              {data!.total.toLocaleString()}
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
}
