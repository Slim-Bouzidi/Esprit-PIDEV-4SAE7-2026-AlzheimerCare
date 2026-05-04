FROM quay.io/keycloak/keycloak:24.0.2

# Set database vendor to mysql
ENV KC_DB=mysql

# Build Keycloak to include the mysql driver in the optimized image
RUN /opt/keycloak/bin/kc.sh build

COPY themes/ /opt/keycloak/themes/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]

