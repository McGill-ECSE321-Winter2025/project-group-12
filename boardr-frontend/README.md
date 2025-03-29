Complete front-end setup

>[!IMPORTANT]
> ## Steps:
>1. Download Node.js
>2. `cd .\boardr-frontend\`
>3. `node -v`
>4. `npm -v`
>4.1 IF ERROR: run `Get-ExecutionPolicy`, check if **Restricted**, then run `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned`
>5. `npm install`
>6. `npm run dev`
>7. Go to http://localhost:5173
>8. Develop!

>[!WARNING]
>To log in/register you need to also run the backend using `./gradlew build` `./gradlew bootJar`

>[!NOTE]
> ## Documentation:
>- Framework: Vue.js
>- HTTP requests: Axios
>- Component library: PrimeVue
>- CSS: TailwindCss
> ## Hierarchy:
>- index.html
>- main.js
>- App.vue
>- src/router/index.js
>- src/views/
>- src/components

>[!TIP] 
>There is a Devtools button at the bottom of the page so you can see the components by hovering
>Added CORS config to connect 5173 <-> 8080

>[!CAUTION]
> ## TODO:
>Implement @Rampex1's Figma design for all pages
>Make sure that the frontend and backend communicate properly through endpoints
>Make sure data elements are rendered properly on the page
>Implement Spring Security/Auth?
>Improve NavBar

---
## How it looks so far: 
`http://localhost:5173/`
![image](https://github.com/user-attachments/assets/7a690625-e7c6-4e3f-9a4e-8728c739655c)
`http://localhost:5173/events`
![image](https://github.com/user-attachments/assets/88ca0aa4-eba0-4c30-8fbe-d51f5a5e0f34)
`http://localhost:5173/games`
![image](https://github.com/user-attachments/assets/f4a0b620-8211-416e-9307-3f56d4624f8e)
`http://localhost:5173/login`
![image](https://github.com/user-attachments/assets/1d38c8bf-5701-4d54-8a23-aece8063cb28)

`http://localhost:5173/register`
![image](https://github.com/user-attachments/assets/a84fa0e6-d9ed-4aaa-a531-75c31a35c192)

`http://localhost:5173/accounts`
![image](https://github.com/user-attachments/assets/49001c76-9075-4290-9c33-3090278bad0e)
