import { useState } from "react";

const data = {
  microservices: [
    {
      name: "Order Service",
      icon: "📦",
      color: "#FF6B35",
      desc: "Core order lifecycle management",
      endpoints: ["POST /orders", "GET /orders/:id", "PATCH /orders/:id/status"],
      db: "orders_db",
      kafkaTopics: ["order.created", "order.updated", "order.cancelled"],
    },
    {
      name: "Inventory Service",
      icon: "🏭",
      color: "#00D9A3",
      desc: "Stock tracking & reservation",
      endpoints: ["GET /stock/:sku", "POST /reserve", "POST /release"],
      db: "inventory_db",
      kafkaTopics: ["inventory.reserved", "inventory.depleted", "inventory.restocked"],
    },
    {
      name: "Fulfillment Service",
      icon: "🚚",
      color: "#4B8EFF",
      desc: "Warehouse routing & shipping",
      endpoints: ["POST /fulfill", "GET /shipments/:id", "POST /assign-warehouse"],
      db: "fulfillment_db",
      kafkaTopics: ["shipment.created", "shipment.dispatched", "shipment.delivered"],
    },
    {
      name: "Notification Service",
      icon: "🔔",
      color: "#FFD93D",
      desc: "Email, SMS & push alerts",
      endpoints: ["POST /notify", "GET /templates", "POST /bulk-notify"],
      db: "notifications_db",
      kafkaTopics: ["notification.sent", "notification.failed"],
    },
    {
      name: "Payment Service",
      icon: "💳",
      color: "#C77DFF",
      desc: "Payment capture & refunds",
      endpoints: ["POST /charge", "POST /refund", "GET /transactions/:id"],
      db: "payments_db",
      kafkaTopics: ["payment.captured", "payment.refunded", "payment.failed"],
    },
    {
      name: "IoT Gateway",
      icon: "📡",
      color: "#FF4D6D",
      desc: "Sensor data ingestion & alerts",
      endpoints: ["POST /telemetry", "GET /sensors", "GET /alerts"],
      db: "iot_db",
      kafkaTopics: ["sensor.temperature", "sensor.weight", "iot.alert"],
    },
  ],
  orderStates: [
    { id: "PENDING", color: "#FFD93D", desc: "Order received, awaiting payment" },
    { id: "PAYMENT_CAPTURED", color: "#4B8EFF", desc: "Payment confirmed" },
    { id: "INVENTORY_RESERVED", color: "#00D9A3", desc: "Stock locked for order" },
    { id: "PROCESSING", color: "#FF6B35", desc: "Picking & packing in warehouse" },
    { id: "SHIPPED", color: "#C77DFF", desc: "In transit with carrier" },
    { id: "DELIVERED", color: "#00D9A3", desc: "Successfully delivered" },
    { id: "CANCELLED", color: "#FF4D6D", desc: "Order cancelled / reversed" },
    { id: "REFUNDED", color: "#FF4D6D", desc: "Payment returned to customer" },
  ],
  techStack: [
    { layer: "Frontend", items: ["React 18", "Redux Toolkit", "Recharts", "TailwindCSS", "MQTT.js"] },
    { layer: "API Gateway", items: ["Spring Cloud Gateway", "JWT Auth", "Rate Limiting", "SSL Termination"] },
    { layer: "Microservices", items: ["Spring Boot 3", "Spring State Machine", "JPA/Hibernate", "Feign Client"] },
    { layer: "Messaging", items: ["Apache Kafka", "Schema Registry", "Kafka Streams", "Dead Letter Queue"] },
    { layer: "Database", items: ["PostgreSQL 15", "Redis Cache", "InfluxDB (IoT)", "Elasticsearch"] },
    { layer: "DevOps", items: ["Docker", "Kubernetes", "GitHub Actions", "Prometheus + Grafana"] },
  ],
  addons: [
    {
      title: "State Machine (Spring)",
      icon: "⚙️",
      color: "#FF6B35",
      detail: "Order transitions enforced via Spring State Machine. Guards prevent illegal state jumps (e.g., SHIPPED → PENDING). Actions trigger Kafka events on each transition. Persisted state survives restarts.",
    },
    {
      title: "Warehouse Routing",
      icon: "🗺️",
      color: "#00D9A3",
      detail: "Multi-warehouse assignment engine: selects nearest warehouse with available stock using haversine distance + inventory check. Priority rules: perishable → climate zones, oversized → freight hubs.",
    },
    {
      title: "IoT Dashboard",
      icon: "📡",
      color: "#4B8EFF",
      detail: "Real-time sensor ingestion via MQTT → Kafka → InfluxDB. React admin shows live: temperature (cold chain), weight sensors (package verification), conveyor belt throughput, and anomaly alerts.",
    },
  ],
};

const stateArrows = [0, 1, 2, 3, 4, 5];

export default function OMS() {
  const [activeTab, setActiveTab] = useState("overview");
  const [activeService, setActiveService] = useState(null);
  const [hoveredState, setHoveredState] = useState(null);

  const tabs = ["overview", "services", "state machine", "tech stack", "add-ons"];

  return (
    <div style={{
      fontFamily: "'IBM Plex Mono', 'Courier New', monospace",
      background: "#0A0C10",
      minHeight: "100vh",
      color: "#E8EAF0",
      padding: "0",
      overflowX: "hidden",
    }}>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:wght@300;400;500;600;700&family=Space+Grotesk:wght@400;600;700&display=swap');
        
        * { box-sizing: border-box; margin: 0; padding: 0; }

        .tab-btn {
          background: transparent;
          border: 1px solid #1E2430;
          color: #6B7280;
          padding: 8px 18px;
          cursor: pointer;
          font-family: 'IBM Plex Mono', monospace;
          font-size: 11px;
          letter-spacing: 0.08em;
          text-transform: uppercase;
          transition: all 0.2s;
          border-radius: 2px;
        }
        .tab-btn:hover { color: #E8EAF0; border-color: #3A4050; }
        .tab-btn.active { color: #FF6B35; border-color: #FF6B35; background: rgba(255,107,53,0.07); }

        .service-card {
          background: #0E1117;
          border: 1px solid #1E2430;
          border-radius: 4px;
          padding: 18px;
          cursor: pointer;
          transition: all 0.25s;
        }
        .service-card:hover { border-color: #3A4050; transform: translateY(-2px); }
        .service-card.active { border-color: var(--accent); background: rgba(255,255,255,0.03); }

        .state-pill {
          border-radius: 3px;
          padding: 10px 14px;
          cursor: pointer;
          transition: all 0.2s;
          border: 1px solid transparent;
          font-size: 11px;
          font-family: 'IBM Plex Mono', monospace;
          letter-spacing: 0.05em;
        }
        .state-pill:hover { transform: scale(1.03); }

        .tech-row {
          display: flex;
          align-items: flex-start;
          gap: 16px;
          padding: 14px 0;
          border-bottom: 1px solid #1A1E27;
        }
        .tech-tag {
          background: #1A1E27;
          border: 1px solid #252B38;
          border-radius: 3px;
          padding: 4px 10px;
          font-size: 11px;
          color: #9CA3AF;
          white-space: nowrap;
        }

        .addon-card {
          background: #0E1117;
          border: 1px solid #1E2430;
          border-radius: 4px;
          padding: 22px;
          transition: border-color 0.2s;
        }
        .addon-card:hover { border-color: #3A4050; }

        .kafka-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          display: inline-block;
          margin-right: 8px;
          animation: pulse 2s infinite;
        }
        @keyframes pulse {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.4; }
        }

        .grid-bg {
          background-image: linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px),
            linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
          background-size: 40px 40px;
        }

        .arrow-right::after {
          content: '→';
          color: #3A4050;
          font-size: 18px;
          margin: 0 6px;
        }

        .badge {
          font-size: 10px;
          padding: 2px 8px;
          border-radius: 2px;
          font-family: 'IBM Plex Mono', monospace;
          letter-spacing: 0.05em;
        }
      `}</style>

      {/* Header */}
      <div className="grid-bg" style={{ borderBottom: "1px solid #1E2430", padding: "40px 40px 28px" }}>
        <div style={{ maxWidth: 1100, margin: "0 auto" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 10 }}>
            <span style={{ fontSize: 11, color: "#FF6B35", letterSpacing: "0.15em", textTransform: "uppercase" }}>Project Blueprint</span>
            <span style={{ color: "#2A303C", fontSize: 11 }}>///</span>
            <span style={{ fontSize: 11, color: "#4B5563", letterSpacing: "0.1em" }}>BACKEND SYSTEM</span>
          </div>
          <h1 style={{
            fontFamily: "'Space Grotesk', sans-serif",
            fontSize: "clamp(28px, 4vw, 46px)",
            fontWeight: 700,
            color: "#F1F3F8",
            lineHeight: 1.1,
            letterSpacing: "-0.02em",
            marginBottom: 10,
          }}>
            E-Commerce Order<br/>
            <span style={{ color: "#FF6B35" }}>Management System</span>
          </h1>
          <p style={{ color: "#6B7280", fontSize: 13, maxWidth: 560, lineHeight: 1.6 }}>
            A production-grade backend engine for order lifecycle, inventory tracking, and fulfillment — built with event-driven microservices.
          </p>

          {/* Quick stats */}
          <div style={{ display: "flex", gap: 32, marginTop: 24, flexWrap: "wrap" }}>
            {[
              { label: "Microservices", val: "6" },
              { label: "Kafka Topics", val: "14+" },
              { label: "Order States", val: "8" },
              { label: "DB per Service", val: "✓" },
              { label: "IoT Dashboard", val: "✓" },
            ].map(s => (
              <div key={s.label}>
                <div style={{ fontFamily: "'Space Grotesk', sans-serif", fontSize: 22, fontWeight: 700, color: "#E8EAF0" }}>{s.val}</div>
                <div style={{ fontSize: 10, color: "#4B5563", letterSpacing: "0.1em", textTransform: "uppercase" }}>{s.label}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ borderBottom: "1px solid #1E2430", padding: "0 40px" }}>
        <div style={{ maxWidth: 1100, margin: "0 auto", display: "flex", gap: 8, padding: "12px 0", flexWrap: "wrap" }}>
          {tabs.map(t => (
            <button key={t} className={`tab-btn ${activeTab === t ? "active" : ""}`} onClick={() => setActiveTab(t)}>
              {t}
            </button>
          ))}
        </div>
      </div>

      {/* Content */}
      <div style={{ maxWidth: 1100, margin: "0 auto", padding: "36px 40px" }}>

        {/* OVERVIEW TAB */}
        {activeTab === "overview" && (
          <div>
            <div style={{ marginBottom: 28 }}>
              <p style={{ color: "#9CA3AF", fontSize: 13, lineHeight: 1.8, maxWidth: 720 }}>
                This OMS is <strong style={{ color: "#E8EAF0" }}>not a storefront</strong> — it's the backend engine powering the entire post-checkout journey.
                It handles order ingestion, payment verification, inventory reservation, warehouse assignment, fulfillment, and IoT monitoring — all via asynchronous Kafka events.
              </p>
            </div>

            {/* Architecture diagram */}
            <div style={{ background: "#0E1117", border: "1px solid #1E2430", borderRadius: 6, padding: 28, marginBottom: 24 }}>
              <div style={{ fontSize: 11, color: "#4B5563", letterSpacing: "0.12em", textTransform: "uppercase", marginBottom: 20 }}>
                System Architecture
              </div>

              {/* Flow */}
              <div style={{ display: "flex", alignItems: "center", flexWrap: "wrap", gap: 4, marginBottom: 28 }}>
                {["Client / Storefront", "API Gateway", "Kafka Bus", "Microservices", "PostgreSQL DBs"].map((n, i) => (
                  <div key={n} style={{ display: "flex", alignItems: "center" }}>
                    <div style={{
                      background: i === 2 ? "rgba(255,107,53,0.12)" : "#131720",
                      border: `1px solid ${i === 2 ? "#FF6B35" : "#252B38"}`,
                      borderRadius: 3,
                      padding: "8px 14px",
                      fontSize: 11,
                      color: i === 2 ? "#FF6B35" : "#9CA3AF",
                    }}>{n}</div>
                    {i < 4 && <span style={{ color: "#3A4050", margin: "0 4px", fontSize: 14 }}>→</span>}
                  </div>
                ))}
              </div>

              {/* Services row */}
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(160px, 1fr))", gap: 10 }}>
                {data.microservices.map(s => (
                  <div key={s.name} style={{
                    background: "#131720",
                    border: `1px solid ${s.color}33`,
                    borderLeft: `3px solid ${s.color}`,
                    borderRadius: 3,
                    padding: "10px 12px",
                  }}>
                    <div style={{ fontSize: 16, marginBottom: 4 }}>{s.icon}</div>
                    <div style={{ fontSize: 11, color: s.color, fontWeight: 600 }}>{s.name}</div>
                    <div style={{ fontSize: 10, color: "#4B5563", marginTop: 2, lineHeight: 1.4 }}>{s.desc}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* Key design decisions */}
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
              {[
                { title: "Event-Driven Core", body: "All service communication happens via Kafka topics. No direct REST calls between services — decoupled, resilient, replay-able.", icon: "⚡" },
                { title: "DB per Service", body: "Each microservice owns its PostgreSQL schema. No shared DB, no cross-service joins. Boundaries are hard.", icon: "🗃️" },
                { title: "Outbox Pattern", body: "DB writes + Kafka publish are atomic via transactional outbox. Prevents lost events on crash.", icon: "📬" },
                { title: "CQRS Ready", body: "Read models can be built separately from write models. Admin panel queries from optimized read replicas or Elasticsearch.", icon: "↔️" },
              ].map(d => (
                <div key={d.title} style={{ background: "#0E1117", border: "1px solid #1E2430", borderRadius: 4, padding: 20 }}>
                  <div style={{ fontSize: 22, marginBottom: 8 }}>{d.icon}</div>
                  <div style={{ fontFamily: "'Space Grotesk', sans-serif", fontWeight: 600, color: "#E8EAF0", marginBottom: 6 }}>{d.title}</div>
                  <div style={{ fontSize: 12, color: "#6B7280", lineHeight: 1.7 }}>{d.body}</div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* SERVICES TAB */}
        {activeTab === "services" && (
          <div>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))", gap: 14 }}>
              {data.microservices.map(s => (
                <div
                  key={s.name}
                  className={`service-card ${activeService === s.name ? "active" : ""}`}
                  style={{ "--accent": s.color }}
                  onClick={() => setActiveService(activeService === s.name ? null : s.name)}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 10 }}>
                    <div>
                      <div style={{ fontSize: 22, marginBottom: 6 }}>{s.icon}</div>
                      <div style={{ fontFamily: "'Space Grotesk', sans-serif", fontWeight: 700, color: s.color, fontSize: 14 }}>{s.name}</div>
                      <div style={{ fontSize: 11, color: "#6B7280", marginTop: 2 }}>{s.desc}</div>
                    </div>
                    <div style={{ background: `${s.color}18`, border: `1px solid ${s.color}44`, borderRadius: 2, padding: "2px 8px", fontSize: 10, color: s.color }}>
                      {s.db}
                    </div>
                  </div>

                  {activeService === s.name && (
                    <div style={{ marginTop: 14, paddingTop: 14, borderTop: "1px solid #1E2430" }}>
                      <div style={{ fontSize: 10, color: "#4B5563", letterSpacing: "0.1em", textTransform: "uppercase", marginBottom: 8 }}>Endpoints</div>
                      {s.endpoints.map(e => (
                        <div key={e} style={{ fontSize: 11, color: "#9CA3AF", padding: "4px 0", borderBottom: "1px solid #131720" }}>
                          <span style={{ color: e.startsWith("GET") ? "#00D9A3" : e.startsWith("POST") ? "#4B8EFF" : "#FFD93D", marginRight: 8 }}>
                            {e.split(" ")[0]}
                          </span>
                          {e.split(" ")[1]}
                        </div>
                      ))}
                      <div style={{ fontSize: 10, color: "#4B5563", letterSpacing: "0.1em", textTransform: "uppercase", margin: "12px 0 8px" }}>Kafka Topics</div>
                      {s.kafkaTopics.map(t => (
                        <div key={t} style={{ fontSize: 11, color: "#6B7280", padding: "3px 0", display: "flex", alignItems: "center" }}>
                          <span className="kafka-dot" style={{ background: s.color }}></span>
                          {t}
                        </div>
                      ))}
                    </div>
                  )}

                  {activeService !== s.name && (
                    <div style={{ marginTop: 12, fontSize: 10, color: "#3A4050" }}>
                      {s.kafkaTopics.length} topics · {s.endpoints.length} endpoints — click to expand
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* STATE MACHINE TAB */}
        {activeTab === "state machine" && (
          <div>
            <p style={{ color: "#6B7280", fontSize: 13, marginBottom: 28, lineHeight: 1.7 }}>
              Spring State Machine enforces valid order transitions. Each state change triggers a Kafka event, updates the order DB record, and may invoke compensating actions on failure.
            </p>

            {/* State flow */}
            <div style={{ display: "flex", flexWrap: "wrap", gap: 10, alignItems: "center", marginBottom: 32 }}>
              {data.orderStates.map((s, i) => (
                <div key={s.id} style={{ display: "flex", alignItems: "center" }}>
                  <div
                    className="state-pill"
                    style={{
                      background: hoveredState === s.id ? `${s.color}22` : "#0E1117",
                      border: `1px solid ${hoveredState === s.id ? s.color : "#252B38"}`,
                      color: hoveredState === s.id ? s.color : "#9CA3AF",
                    }}
                    onMouseEnter={() => setHoveredState(s.id)}
                    onMouseLeave={() => setHoveredState(null)}
                  >
                    {s.id}
                  </div>
                  {i < data.orderStates.length - 1 && i !== 5 && (
                    <span style={{ color: "#2A303C", margin: "0 4px" }}>→</span>
                  )}
                  {i === 5 && <span style={{ color: "#2A303C", margin: "0 4px" }}>·</span>}
                </div>
              ))}
            </div>

            {/* Hover detail */}
            {hoveredState && (
              <div style={{ background: "#0E1117", border: `1px solid ${data.orderStates.find(s => s.id === hoveredState)?.color}44`, borderRadius: 4, padding: 16, marginBottom: 24 }}>
                <div style={{ color: data.orderStates.find(s => s.id === hoveredState)?.color, fontWeight: 600, marginBottom: 6 }}>{hoveredState}</div>
                <div style={{ fontSize: 13, color: "#9CA3AF" }}>{data.orderStates.find(s => s.id === hoveredState)?.desc}</div>
              </div>
            )}

            {/* Transitions table */}
            <div style={{ background: "#0E1117", border: "1px solid #1E2430", borderRadius: 4, overflow: "hidden" }}>
              <div style={{ padding: "10px 18px", borderBottom: "1px solid #1E2430", display: "grid", gridTemplateColumns: "1fr 1fr 1fr 1fr", gap: 10 }}>
                {["From", "Event", "To", "Side Effect"].map(h => (
                  <div key={h} style={{ fontSize: 10, color: "#4B5563", textTransform: "uppercase", letterSpacing: "0.1em" }}>{h}</div>
                ))}
              </div>
              {[
                ["PENDING", "payment.captured", "PAYMENT_CAPTURED", "Reserve inventory"],
                ["PAYMENT_CAPTURED", "inventory.reserved", "INVENTORY_RESERVED", "Assign warehouse"],
                ["INVENTORY_RESERVED", "warehouse.assigned", "PROCESSING", "Start picking job"],
                ["PROCESSING", "picking.complete", "SHIPPED", "Create shipment label"],
                ["SHIPPED", "delivery.confirmed", "DELIVERED", "Release reservation"],
                ["ANY", "cancel.requested", "CANCELLED", "Saga: refund + release stock"],
              ].map(([from, event, to, effect], i) => (
                <div key={i} style={{
                  padding: "12px 18px",
                  borderBottom: "1px solid #131720",
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr 1fr 1fr",
                  gap: 10,
                  background: i % 2 === 0 ? "transparent" : "#0B0D12",
                }}>
                  <div style={{ fontSize: 11, color: "#FF6B35" }}>{from}</div>
                  <div style={{ fontSize: 11, color: "#4B8EFF" }}>{event}</div>
                  <div style={{ fontSize: 11, color: "#00D9A3" }}>{to}</div>
                  <div style={{ fontSize: 11, color: "#6B7280" }}>{effect}</div>
                </div>
              ))}
            </div>

            <div style={{ marginTop: 20, padding: 16, background: "rgba(75,142,255,0.06)", border: "1px solid #4B8EFF33", borderRadius: 4 }}>
              <div style={{ fontSize: 11, color: "#4B8EFF", fontWeight: 600, marginBottom: 6 }}>💡 Saga Pattern for Cancellation</div>
              <div style={{ fontSize: 12, color: "#6B7280", lineHeight: 1.7 }}>
                On CANCEL, a compensating transaction saga fires: (1) halt fulfillment, (2) release inventory reservation, (3) trigger payment refund. Each step publishes its own Kafka event. If step 2 fails, step 1 is reversed automatically.
              </div>
            </div>
          </div>
        )}

        {/* TECH STACK TAB */}
        {activeTab === "tech stack" && (
          <div>
            {data.techStack.map(layer => (
              <div key={layer.layer} className="tech-row">
                <div style={{ minWidth: 120, fontSize: 11, color: "#FF6B35", letterSpacing: "0.08em", textTransform: "uppercase", paddingTop: 4 }}>
                  {layer.layer}
                </div>
                <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
                  {layer.items.map(item => (
                    <div key={item} className="tech-tag">{item}</div>
                  ))}
                </div>
              </div>
            ))}

            <div style={{ marginTop: 28, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
              <div style={{ background: "#0E1117", border: "1px solid #1E2430", borderRadius: 4, padding: 20 }}>
                <div style={{ fontSize: 11, color: "#4B5563", textTransform: "uppercase", letterSpacing: "0.1em", marginBottom: 14 }}>Why Spring Boot?</div>
                <div style={{ fontSize: 12, color: "#6B7280", lineHeight: 1.8 }}>
                  Spring's ecosystem (State Machine, Kafka Streams, JPA, Cloud Gateway, Actuator) covers every layer of this OMS natively. Mature, battle-tested, and hiring-relevant for enterprise Java roles.
                </div>
              </div>
              <div style={{ background: "#0E1117", border: "1px solid #1E2430", borderRadius: 4, padding: 20 }}>
                <div style={{ fontSize: 11, color: "#4B5563", textTransform: "uppercase", letterSpacing: "0.1em", marginBottom: 14 }}>Why Kafka?</div>
                <div style={{ fontSize: 12, color: "#6B7280", lineHeight: 1.8 }}>
                  Durable event log means full order audit trail and replay capability. Decouples services completely. Kafka Streams enables real-time analytics (order throughput, SLA monitoring) without a separate pipeline.
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ADD-ONS TAB */}
        {activeTab === "add-ons" && (
          <div style={{ display: "grid", gap: 20 }}>
            {data.addons.map(a => (
              <div key={a.title} className="addon-card">
                <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 14 }}>
                  <span style={{ fontSize: 28 }}>{a.icon}</span>
                  <div>
                    <div style={{ fontFamily: "'Space Grotesk', sans-serif", fontWeight: 700, color: a.color, fontSize: 16 }}>{a.title}</div>
                  </div>
                </div>
                <p style={{ fontSize: 13, color: "#9CA3AF", lineHeight: 1.8 }}>{a.detail}</p>
              </div>
            ))}

            {/* IoT sensor mockup */}
            <div style={{ background: "#0E1117", border: "1px solid #1E2430", borderRadius: 4, padding: 20 }}>
              <div style={{ fontSize: 11, color: "#4B5563", textTransform: "uppercase", letterSpacing: "0.1em", marginBottom: 16 }}>IoT Live Dashboard (Simulated)</div>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(160px, 1fr))", gap: 12 }}>
                {[
                  { label: "Cold Chain Temp", val: "2.4°C", status: "ok", color: "#00D9A3" },
                  { label: "Pkg Weight", val: "1.32 kg", status: "ok", color: "#4B8EFF" },
                  { label: "Conveyor Speed", val: "0.8 m/s", status: "ok", color: "#4B8EFF" },
                  { label: "Dock Sensor 4", val: "ALERT", status: "alert", color: "#FF4D6D" },
                  { label: "Humidity Zone A", val: "44%", status: "ok", color: "#00D9A3" },
                  { label: "Throughput/hr", val: "324 pkg", status: "ok", color: "#FFD93D" },
                ].map(sensor => (
                  <div key={sensor.label} style={{
                    background: "#0B0D12",
                    border: `1px solid ${sensor.color}33`,
                    borderRadius: 3,
                    padding: "12px 14px",
                  }}>
                    <div style={{ fontSize: 10, color: "#4B5563", marginBottom: 6 }}>{sensor.label}</div>
                    <div style={{ fontFamily: "'Space Grotesk', sans-serif", fontWeight: 700, color: sensor.color, fontSize: 18 }}>{sensor.val}</div>
                    <div style={{ fontSize: 10, color: sensor.status === "alert" ? "#FF4D6D" : "#3A4050", marginTop: 4 }}>
                      <span className="kafka-dot" style={{ background: sensor.color, width: 6, height: 6 }}></span>
                      {sensor.status === "alert" ? "ALERT" : "live"}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Footer */}
      <div style={{ borderTop: "1px solid #1E2430", padding: "16px 40px", display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 10 }}>
        <div style={{ fontSize: 10, color: "#2A303C", letterSpacing: "0.1em" }}>E-COMMERCE OMS · MICROSERVICES BLUEPRINT</div>
        <div style={{ display: "flex", gap: 10 }}>
          {["Spring Boot", "Kafka", "PostgreSQL", "React"].map(t => (
            <span key={t} style={{ fontSize: 10, color: "#3A4050", background: "#131720", padding: "3px 8px", borderRadius: 2 }}>{t}</span>
          ))}
        </div>
      </div>
    </div>
  );
}
