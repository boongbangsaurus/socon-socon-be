FROM nginx:latest

RUN rm -rf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/nginx.conf
COPY nginx_80.conf /etc/nginx/conf.d/nginx_80.conf
COPY nginx_443.conf /etc/nginx/conf.d/nginx_443.conf
COPY policy/privacy_policy.html /usr/share/nginx/html/policy/privacy_policy.html
COPY policy/privacy_policy_history.html /usr/share/nginx/html/policy/privacy_policy_history.html
COPY policy/history /usr/share/nginx/html/policy/history

CMD ["nginx", "-g", "daemon off;"]
