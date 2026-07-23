FROM quay.io/keycloak/keycloak:26.6.4 as builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_FEATURES=token-exchange,admin-fine-grained-authz

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:26.6.4

COPY --from=builder /opt/keycloak/lib/quarkus/runtime/lib/main/*.jar /opt/keycloak/lib/quarkus/runtime/lib/main/

COPY realm-export.json /opt/keycloak/data/import/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
