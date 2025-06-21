// frontend/app/categories/price-stats/page.tsx
"use client";

import { useRouter } from "next/navigation";
import { FormEvent, useState } from "react";
import { CATEGORIES } from "../../../lib/categories";

export default function CategorySearch() {
  const router = useRouter();
  const [cat, setCat] = useState<string>("");

  const go = (e: FormEvent) => {
    e.preventDefault();
    if (!cat) return;
    router.push(`/categories/${encodeURIComponent(cat)}/price-stats`);
  };

  return (
    <section className="space-y-4">
      <h1 className="text-xl font-bold">카테고리별 최저·최고가 조회</h1>

      <form onSubmit={go} className="flex items-center gap-3">
        <label htmlFor="category" className="sr-only">
          카테고리 선택
        </label>
        <select
          id="category"
          value={cat}
          onChange={(e) => setCat(e.target.value)}
          className="border p-2 rounded flex-1"
          required
        >
          <option value="" disabled>
            — 카테고리 선택 —
          </option>
          {CATEGORIES.map((kr) => (
            <option key={kr} value={kr}>
              {kr}
            </option>
          ))}
        </select>
        <button
          type="submit"
          disabled={!cat}
          className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50"
        >
          조회
        </button>
      </form>

      <p className="text-gray-500">
        드롭다운에서 카테고리를 선택하고 “조회” 버튼을 누르면 해당 카테고리의
        최저가·최고가가 표시됩니다.
      </p>
    </section>
  );
}
