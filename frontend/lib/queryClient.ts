// frontend/lib/queryClient.ts
import { QueryClient } from "@tanstack/react-query";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // 실패 시 1번 재시도
      retry: 1,
      // 캐시 만료 시간을 0으로 두어 항상 fetch
      staleTime: 0,
      // 마운트될 때마다(페이지 전환 시에도) 무조건 재요청
      refetchOnMount: "always",
      // 창이 포커스될 때마다 자동 재요청
      refetchOnWindowFocus: "always",
      // 네트워크가 복구될 때마다 자동 재요청
      refetchOnReconnect: "always",
      // 이전 데이터 대신 로딩 상태부터 시작하고 싶으면
      // keepPreviousData 대신 placeholderData를 빈 값으로 주입
      placeholderData: undefined,
      // (선택) 주기 폴링
      // refetchInterval: 5_000,
    },
  },
});
