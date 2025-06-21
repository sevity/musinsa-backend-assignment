"use client";
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";

type Item = { category: string; brand: string; price: number };
type Resp = { items: Item[]; total: number };

export default function CheapestByCategory() {
  const { data, isLoading, error } = useQuery<Resp>({
    queryKey: ["cheapestByCat"],
    queryFn: () => api.get("/categories/cheapest-brands").then(r => r.data)
  });

  if (isLoading) return <p>Loading…</p>;
  if (error) return <p className="text-red-600">에러: {(error as any).message}</p>;

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
          {data!.items.map(it => (
            <tr key={it.category}>
              <td className="p-2">{it.category}</td>
              <td className="p-2">{it.brand}</td>
              <td className="p-2 text-right">{it.price.toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr className="font-bold">
            <td className="p-2" colSpan={2}>총액</td>
            <td className="p-2 text-right">{data!.total.toLocaleString()}</td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
}
