import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
 
export default defineConfig({
  plugins: [react()],

  server: {
    proxy: {
      // 경로가 /front로 시작하면 아래 target 주소로 전달합니다.
      '/front': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 백엔드 API 주소에 /front가 없다면 제거해주는 설정입니다.
        // 만약 백엔드 주소 자체가 http://...:8080/front/... 라면 아래 라인을 지워라
        rewrite: (path) => path.replace(/^\/front/, ''),
      },
    },
  },
})

 
/*
- npm run dev   실행 시: Vite가 자동으로 .env.development 읽어서 baseURL을 설정합니다.(개발환경)
- npm run build 실행 시: Vite가 자동으로 .env.production  읽어서 baseURL을 설정합니다.(운영환경)

[개발환경]
- 클라이언트에서 axios.js의 api메서드를 통해 API 요청을 할 때, baseURL을 .env.development에서 '/front'로 설정했기 때문에, 클라이언트는 '/front/test'와 같이 요청을 보냅니다. 
- rewrite 설정이 적용되어 백엔드 API 주소에 /front가 없다면 제거해주는 설정이 있기 때문에, 실제로는 'http://localhost:8080/test'로 요청이 전달됩니다.
- Vite의 프록시 설정이 '/front'로 시작하는 요청을 감지하여 'http://localhost:8080'으로 전달합니다.

[운영환경]
- 운영환경에서는 Vite의 프록시 설정이 적용되지 않으므로, 클라이언트에서 API 요청을 할 때 .env.production에서 설정한 실제 백엔드 API 주소로 요청이 직접 전달됩니다.
- nginx 설정에서 프록시를 통해 API 요청을 백엔드 서버로 전달하도록 설정되어 있다면, 클라이언트는 여전히 '/front/test'와 같이 요청을 보낼 수 있습니다. nginx가 이를 감지하여 백엔드 서버로 전달합니다.
*/
