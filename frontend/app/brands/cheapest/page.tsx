"use client";
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";

type CatPrice = { category: string; price: number };
type Resp = { brand: string; categories: CatPrice[]; total: number };

export default function CheapestBrand() {
  const { data, isLoading, error } = useQuery<Resp>({
    queryKey: ["cheapestBrand"],
    queryFn: () => api.get("/brands/cheapest").then(r => r.data)
  });

  if (isLoading) return <p>Loading…</p>;
  if (error) return <p className="text-red-600">에러: {(error as any).message}</p>;

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold">단일 브랜드 최저가 – {data!.brand}</h2>
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-gray-100">
            <th className="p-2 text-left">카테고리</th>
            <th className="p-2 text-right">가격</th>
          </tr>
        </thead>
        <tbody>
          {data!.categories.map(c => (
            <tr key={c.category}>
              <td className="p-2">{c.category}</td>
              <td className="p-2 text-right">{c.price.toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr className="font-bold">
            <td className="p-2">총액</td>
            <td className="p-2 text-right">{data!.total.toLocaleString()}</td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
}
