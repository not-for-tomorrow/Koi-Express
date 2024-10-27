# Step 1: Use Node 18 to build the Vite application
FROM node:18 AS build

# Step 2: Set the working directory in the container
WORKDIR /app

# Step 3: Copy the package files and install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Step 4: Copy the rest of the application code to the container
COPY . .

# Step 5: Build the Vite application for production
RUN npm run build

# Step 6: Use a minimal Nginx image to serve the frontend
FROM nginx:alpine

# Step 7: Copy the build output to the Nginx directory
COPY --from=build /app/dist /usr/share/nginx/html

# Step 8: Expose port 80 for the application
EXPOSE 80

# Step 9: Start Nginx
CMD ["nginx", "-g", "daemon off;"]
