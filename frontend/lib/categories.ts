// frontend/lib/categories.ts

/** Musinsa 과제용 8개 카테고리 */
export const CATEGORIES = [
  "상의",
  "아우터",
  "바지",
  "스니커즈",
  "가방",
  "모자",
  "양말",
  "액세서리",
] as const;

/** CATEGORIES 의 값 중 하나 */
export type Category = (typeof CATEGORIES)[number];

/** README 예시와 동일한 샘플 가격 세트 */
export const SAMPLE_PRICES: Record<Category, number> = {
  상의: 10000,
  아우터: 5000,
  바지: 3000,
  스니커즈: 9000,
  가방: 2000,
  모자: 1500,
  양말: 1700,
  액세서리: 1900,
};
