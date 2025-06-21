/* frontend/app/admin/page.tsx */
"use client";

import { Tab } from "@headlessui/react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useState, useEffect } from "react";
import api from "@/lib/api";
import { queryClient } from "@/lib/queryClient";
import { CATEGORIES, SAMPLE_PRICES, Category } from "@/lib/categories";


// 브랜드 상세 조회 시 사용할 타입 정의
type BrandDetail = {
  brand: string;
  prices: Record<Category, number>;
};
// 프론트에서 사용하기 위한 ProductResponse 타입 정의
type ProductResponse = {
  id: number;
  brand: string;
  category: Category;
  price: number;
};

/* ───────────── 공통 유틸 ───────────── */
const cls = (...c: string[]) => c.filter(Boolean).join(" ");

const useCrud = (fn: () => Promise<any>) =>
  useMutation({
    mutationFn: fn,
    onSuccess: () => {
      queryClient.invalidateQueries();
      alert("✅ 성공!");
    },
    onError: (e: any) => alert(e?.response?.data?.message ?? e.message ?? "Error")
  });

function LabeledInput({
  label,
  value,
  onChange,
  placeholder,
  type = "text",
  required = false,
  className = "",
  readOnly = false
}: {
  label: string;
  value: string | number;
  onChange: (v: string) => void;
  placeholder?: string;
  type?: string;
  required?: boolean;
  className?: string;
  readOnly?: boolean;
}) {
  return (
    <div className={`flex items-center gap-2 ${className}`}>
      <label className="w-36 text-sm shrink-0">{label}</label>
      <input
        value={value}
        onChange={e => onChange(e.target.value)}
        placeholder={placeholder}
        type={type}
        required={required}
        readOnly={readOnly}
        className="flex-1 border p-2 rounded"
      />
    </div>
  );
}


/* ───────────── 선택 컴포넌트 ───────────── */
const BrandSelector = ({
  brand,
  setBrand,
  list
}: {
  brand: string;
  setBrand: (s: string) => void;
  list: string[];
}) => (
  <div className="flex items-center gap-2">
    <label className="w-36 text-sm shrink-0">브랜드</label>
    <select
      value={brand}
      onChange={e => setBrand(e.target.value)}
      className="border p-2 rounded flex-1 bg-white"
    >
      <option value="">브랜드 선택</option>
      {list.map(b => (
        <option key={b} value={b}>
          {b}
        </option>
      ))}
    </select>
    <input
      value={brand}
      onChange={e => setBrand(e.target.value)}
      placeholder="직접 입력"
      className="border p-2 rounded flex-1"
    />
  </div>
);

const CategorySelector = ({
  category,
  setCategory
}: {
  category: Category;
  setCategory: (c: Category) => void;
}) => (
  <div className="flex items-center gap-2">
    <label className="w-36 text-sm shrink-0">카테고리</label>
    <select
      value={category}
      onChange={e => setCategory(e.target.value as Category)}
      className="border p-2 rounded flex-1 bg-white"
    >
      {CATEGORIES.map(c => (
        <option key={c} value={c}>
          {c}
        </option>
      ))}
    </select>
  </div>
);

/* ───────────── 메인 ───────────── */
export default function Admin() {
  const tabs = [
    "브랜드 등록",
    "브랜드 수정",
    "브랜드 삭제",
    "상품 등록",
    "상품 수정",
    "상품 삭제"
  ];

  return (
    <section className="space-y-6">
      <h1 className="text-xl font-bold">🛠️ 관리(Admin)</h1>

      <Tab.Group>
        <Tab.List className="flex flex-wrap gap-2">
          {tabs.map(t => (
            <Tab
              key={t}
              className={({ selected }) =>
                cls(
                  "px-4 py-2 rounded text-sm",
                  selected ? "bg-blue-600 text-white" : "bg-gray-200"
                )
              }
            >
              {t}
            </Tab>
          ))}
        </Tab.List>

        <Tab.Panels className="mt-4">
          <Tab.Panel><BrandCreate /></Tab.Panel>
          <Tab.Panel><BrandUpdate /></Tab.Panel>
          <Tab.Panel><BrandDelete /></Tab.Panel>
          <Tab.Panel><ProductCreate /></Tab.Panel>
          <Tab.Panel><ProductUpdate /></Tab.Panel>
          <Tab.Panel><ProductDelete /></Tab.Panel>
        </Tab.Panels>
      </Tab.Group>
    </section>
  );
}

/* ───────────── 브랜드 등록 ───────────── */
export function BrandCreate() {
  const [name, setName] = useState("");
  const [prices, setPrices] = useState<Record<Category, string>>(
    Object.fromEntries(
      CATEGORIES.map((c) => [c, SAMPLE_PRICES[c].toString()])
    ) as Record<Category, string>
  );

  const mut = useMutation({
    mutationFn: () =>
      api.post("/brands", {
        brand: name,
        prices: Object.fromEntries(
          Object.entries(prices).map(([c, p]) => [c, Number(p)])
        ),
      }),
    onSuccess: () => {
      // 최신 데이터 반영을 위해 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: ["cheapestByCat"] });
      queryClient.invalidateQueries({ queryKey: ["brandList"] });
      alert("✅ 등록 성공");
    },
    onError: (e: any) =>
      alert(e.response?.data?.message ?? e.message ?? "Error"),
  });

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        mut.mutate();
      }}
      className="space-y-4 p-4 border rounded"
    >
      <h2 className="font-semibold">브랜드 등록</h2>

      <LabeledInput
        label="브랜드명"
        value={name}
        onChange={setName}
        placeholder="예) Z"
        required
      />

      <fieldset className="border rounded p-2">
        <legend className="text-sm font-medium px-1">가격 세트</legend>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-2">
          {CATEGORIES.map((cat) => (
            <div key={cat} className="flex items-center gap-2">
              <label className="w-24 text-sm">{cat}</label>
              <input
                value={prices[cat]}
                onChange={(e) =>
                  setPrices((prev) => ({
                    ...prev,
                    [cat]: e.target.value,
                  }))
                }
                className="flex-1 border p-2 rounded"
              />
            </div>
          ))}
        </div>
      </fieldset>

      <button type="submit" className="btn-primary">
        ➕ 등록
      </button>
    </form>
  );
}

/* ───────────── 브랜드 수정 ───────────── */
function BrandUpdate() {
  const [origName, setOrigName] = useState("");
  const {
    data,
    isLoading,
    error,
  } = useQuery<BrandDetail, Error>({
    queryKey: ["brand", origName],
    queryFn: () => api.get(`/brands/${encodeURIComponent(origName)}`).then((r) => r.data),
    enabled: !!origName,
  });

  const [prices, setPrices] = useState<Record<Category, string>>(
    Object.fromEntries(CATEGORIES.map((c) => [c, ""])) as any
  );

  useEffect(() => {
    if (data?.prices) {
      setPrices(
        Object.fromEntries(
          CATEGORIES.map((c) => [c, String(data.prices[c] ?? "")])
        ) as any
      );
    }
  }, [data]);

  const mutUpdate = useMutation({
    mutationFn: () =>
      api.put(`/brands/${encodeURIComponent(origName)}`, {
        prices: Object.fromEntries(
          Object.entries(prices)
            .filter(([, v]) => v)
            .map(([c, p]) => [c, Number(p)])
        ),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["brand", origName] });
      alert("✅ 수정 성공");
    },
    onError: (e: any) => alert(e?.response?.data?.message ?? e.message),
  });

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        mutUpdate.mutate();
      }}
      className="space-y-4 p-4 border rounded"
    >
      <h2 className="font-semibold">브랜드 수정</h2>
      <LabeledInput
        label="브랜드명"
        value={origName}
        onChange={setOrigName}
        placeholder="예) Z"
        required
      />
      {isLoading && <p>불러오는 중…</p>}
      {error && <p className="text-red-600">에러: {error.message}</p>}
      {data && (
        <fieldset className="border rounded p-2">
          <legend className="text-sm font-medium px-1">가격 수정</legend>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-2">
            {CATEGORIES.map((cat) => (
              <div key={cat} className="flex items-center gap-2">
                <label className="w-24 text-sm">{cat}</label>
                <input
                  value={prices[cat]}
                  onChange={(e) =>
                    setPrices((prev) => ({ ...prev, [cat]: e.target.value }))
                  }
                  className="flex-1 border p-2 rounded"
                  placeholder="현재 가격"
                />
              </div>
            ))}
          </div>
        </fieldset>
      )}
      <button className="btn-secondary" disabled={!data || isLoading}>
        ✏️ 수정
      </button>
    </form>
  );
}

/* ───────────── 브랜드 삭제 ───────────── */
function BrandDelete() {
  const [name, setName] = useState("");

  const mut = useCrud(() =>
    api.delete(`/brands/${encodeURIComponent(name)}`)
  );

  return (
    <form
      onSubmit={e => {
        e.preventDefault();
        mut.mutate();
      }}
      className="space-y-4 p-4 border rounded"
    >
      <h2 className="font-semibold">브랜드 삭제</h2>

      <LabeledInput
        label="브랜드명"
        value={name}
        onChange={setName}
        required
      />

      <button className="btn-danger">🗑️ 삭제</button>
    </form>
  );
}

/* ───────────── 상품 등록 ───────────── */
function ProductCreate() {
  const { data: brandList = [] } = useQuery<string[]>({
    queryKey: ["brandList"],
    queryFn: () => api.get("/brands").then(r => r.data),
    staleTime: 60000
  });

  const [brand, setBrand] = useState("");
  const [category, setCategory] = useState<Category>(CATEGORIES[0]);
  const [price, setPrice] = useState("10000");

  const mut = useCrud(() =>
    api.post("/products", {
      brand,
      category,
      price: Number(price)
    })
  );

  return (
    <form
      onSubmit={e => {
        e.preventDefault();
        mut.mutate();
      }}
      className="space-y-4 p-4 border rounded"
    >
      <h2 className="font-semibold">상품 등록</h2>

      <BrandSelector brand={brand} setBrand={setBrand} list={brandList} />
      <CategorySelector category={category} setCategory={setCategory} />
      <LabeledInput
        label="가격"
        value={price}
        onChange={setPrice}
        type="number"
        required
      />

      <button className="btn-primary">➕ 등록</button>
    </form>
  );
}

/* ───────────── 상품 수정 ───────────── */
function ProductUpdate() {
  // 1) 상품 목록 조회
  const { data: products = [] } = useQuery<ProductResponse[]>({
    queryKey: ["productList"],
    queryFn: () => api.get("/products").then((r) => r.data),
    staleTime: 60_000,
  });

  // 1-1) 브랜드명 중복 제거
  const brandList = Array.from(new Set(products.map((p) => p.brand)));

  // 2) 선택된 상품 ID
  const [pid, setPid] = useState<number | "">("");

  // 3) 상품 상세 조회
  const { data: prod, isLoading: loadingProd } = useQuery<ProductResponse>({
    queryKey: ["product", pid],
    queryFn: () => api.get(`/products/${pid}`).then((r) => r.data),
    enabled: pid !== "",
  });

  // 4) 가격 상태
  const [price, setPrice] = useState("");

  // 5) prod 변경 시 가격 초기화
  useEffect(() => {
    if (prod) {
      setPrice(String(prod.price));
    }
  }, [prod]);

  // 6) 수정 mutation
  const mut = useMutation({
    mutationFn: () =>
      api.put(`/products/${pid}`, {
        price: Number(price),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["productList"] });
      queryClient.invalidateQueries({ queryKey: ["cheapestByCat"] });
      queryClient.invalidateQueries({ queryKey: ["cheapestBrand"] });
      alert("✅ 수정 성공");
    },
    onError: (e: any) => alert(e.response?.data?.message ?? e.message),
  });

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        mut.mutate();
      }}
      className="space-y-4 p-4 border rounded"
    >
      <h2 className="font-semibold">상품 수정</h2>

      {/* 상품 선택 드롭다운 */}
      <div className="flex items-center gap-2">
        <label className="w-36 text-sm">상품 선택</label>
        <select
          value={pid}
          onChange={(e) =>
            setPid(e.target.value === "" ? "" : Number(e.target.value))
          }
          className="flex-1 border p-2 rounded bg-white"
          required
        >
          <option value="">— 상품 선택 —</option>
          {products.map((p) => (
            <option key={p.id} value={p.id}>
              #{p.id} {p.brand} / {p.category} ({p.price.toLocaleString()}원)
            </option>
          ))}
        </select>
      </div>

      {/* 로딩 & 에러 */}
      {loadingProd && <p>불러오는 중…</p>}

      {/* 상세 폼 (prod 가 있으면) */}
      {prod && (
        <>
          {/* 브랜드/카테고리 readonly 표시 */}
          <LabeledInput
            label="브랜드"
            value={prod.brand}
            onChange={() => {}}
            className="opacity-50"
            readOnly
          />
          <LabeledInput
            label="카테고리"
            value={prod.category}
            onChange={() => {}}
            className="opacity-50"
            readOnly
          />

          {/* 가격만 수정 가능 */}
          <LabeledInput
            label="가격"
            value={price}
            onChange={setPrice}
            type="number"
            required
          />
        </>
      )}

      <button
        type="submit"
        disabled={!prod}
        className="btn-secondary"
      >
        ✏️ 수정
      </button>
    </form>
  );
}



/* ───────────── 상품 삭제 ───────────── */
function ProductDelete() {
  // 1) 상품 목록 조회
  const { data: products = [] } = useQuery<ProductResponse[]>({
    queryKey: ["productList"],
    queryFn: () => api.get("/products").then(r => r.data),
    staleTime: 60_000,
  });

  // 2) 선택된 상품 ID
  const [pid, setPid] = useState<number | "">("");

  // 3) 삭제 mutation
  const delMut = useMutation({
    mutationFn: () => api.delete(`/products/${pid}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["productList"] });
      queryClient.invalidateQueries({ queryKey: ["cheapestByCat"] });
      queryClient.invalidateQueries({ queryKey: ["cheapestBrand"] });
      alert("🗑️ 삭제 성공");
    },
    onError: (e: any) => alert(e.response?.data?.message ?? e.message),
  });

  return (
    <form
      onSubmit={e => {
        e.preventDefault();
        delMut.mutate();
      }}
      className="space-y-4 p-4 border rounded"
    >
      <h2 className="font-semibold">상품 삭제</h2>

      {/* 상품 선택 드롭다운 */}
      <div className="flex items-center gap-2">
        <label className="w-36 text-sm">상품 선택</label>
        <select
          value={pid}
          onChange={e =>
            setPid(e.target.value === "" ? "" : Number(e.target.value))
          }
          className="flex-1 border p-2 rounded bg-white"
          required
        >
          <option value="">— 상품 선택 —</option>
          {products.map(p => (
            <option key={p.id} value={p.id}>
              #{p.id} {p.brand} / {p.category} ({p.price.toLocaleString()}원)
            </option>
          ))}
        </select>
      </div>

      <button
        type="submit"
        disabled={!pid}
        className="btn-danger"
      >
        🗑️ 삭제
      </button>
    </form>
  );
}

/* ───────────── Tailwind 버튼 헬퍼 ───────────── */
const btn =
  "px-4 py-2 rounded text-white whitespace-nowrap disabled:opacity-50";
const btnPrimary = `${btn} bg-blue-600`;
const btnSecondary = `${btn} bg-green-600`;
const btnDanger = `${btn} bg-red-600`;

/* 전역 클래스 주입 (간단 적용) */
if (typeof document !== "undefined") {
  const style = document.createElement("style");
  style.innerHTML = `
    .btn-primary { @apply ${btnPrimary}; }
    .btn-secondary { @apply ${btnSecondary}; }
    .btn-danger { @apply ${btnDanger}; }
  `;
  document.head.appendChild(style);
}
