// screens-trip.jsx — Trip in progress (map + SOS) + Payment

const ScreenTripActive = ({ theme, t, accent, onNav }) => {
  const [sheetOpen, setSheetOpen] = React.useState(true);
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text, position: 'relative' }}>
      {/* Map fills */}
      <div style={{ position: 'absolute', inset: 0 }}>
        <MapPlaceholder theme={theme} accent={accent}/>
      </div>

      {/* Top floating controls */}
      <div style={{ position: 'relative', padding: 16, display: 'flex', justifyContent: 'space-between', zIndex: 2 }}>
        <button onClick={() => onNav('home')} style={{ ...iconBtn(theme), background: theme.surface, boxShadow: theme.shadow }}>
          <Icon name="arrow-left" size={20}/>
        </button>
        <div style={{ display: 'flex', gap: 8 }}>
          <button style={{ ...iconBtn(theme), background: theme.surface, boxShadow: theme.shadow }}><Icon name="message" size={18}/></button>
          <button style={{ ...iconBtn(theme), background: theme.surface, boxShadow: theme.shadow }}><Icon name="phone" size={18}/></button>
        </div>
      </div>

      {/* SOS floating button */}
      <button style={{
        position: 'absolute', right: 16, bottom: sheetOpen ? 320 : 110, zIndex: 3,
        width: 60, height: 60, borderRadius: 30, border: 'none', cursor: 'pointer',
        background: theme.danger, color: '#fff', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        boxShadow: `0 0 0 4px ${theme.bg}, 0 8px 24px rgba(220,38,38,.5)`,
        fontFamily: 'inherit', fontWeight: 800, fontSize: 11, letterSpacing: '.05em',
      }}>
        SOS
      </button>

      {/* Live status pill */}
      <div style={{ position: 'absolute', left: '50%', transform: 'translateX(-50%)', top: 80, zIndex: 2,
        background: theme.surface, padding: '8px 14px', borderRadius: 999, boxShadow: theme.shadow,
        display: 'flex', alignItems: 'center', gap: 8,
      }}>
        <span style={{ width: 8, height: 8, borderRadius: '50%', background: theme.success, boxShadow: `0 0 0 4px ${theme.success}33` }}/>
        <span style={{ fontSize: 12.5, fontWeight: 700, color: theme.text }}>En camino · 12 min</span>
      </div>

      {/* Bottom sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, zIndex: 2,
        background: theme.surface, borderRadius: '24px 24px 0 0',
        boxShadow: '0 -10px 30px rgba(0,0,0,.10)',
        padding: '12px 20px 20px',
        transition: 'transform .25s', transform: sheetOpen ? 'translateY(0)' : 'translateY(220px)',
      }}>
        <button onClick={() => setSheetOpen(!sheetOpen)} style={{ background: 'none', border: 'none', display: 'block', margin: '0 auto 8px', cursor: 'pointer', padding: 4 }}>
          <div style={{ width: 40, height: 4, borderRadius: 2, background: theme.borderStrong }}/>
        </button>

        {/* Driver row */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, paddingBottom: 12, borderBottom: `1px solid ${theme.border}` }}>
          <Avatar name="Diego" size={48} hue={200}/>
          <div style={{ flex: 1 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <span style={{ fontSize: 15, fontWeight: 700 }}>Diego A.</span>
              <Icon name="check-circle" size={14} color={accent}/>
            </div>
            <div style={{ fontSize: 12, color: theme.textMuted, marginTop: 1, display: 'flex', alignItems: 'center', gap: 6 }}>
              <Icon name="star" size={11} color={accent}/> 4.8 · Toyota Yaris · ABC-123
            </div>
          </div>
          <span style={{
            padding: '4px 10px', borderRadius: 999, background: theme.accentSoft, color: accent,
            fontSize: 11, fontWeight: 700, letterSpacing: '.04em',
          }}>PIN 4729</span>
        </div>

        {/* Route summary */}
        <div style={{ paddingTop: 12, paddingBottom: 12, display: 'flex', gap: 12 }}>
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', paddingTop: 6 }}>
            <div style={{ width: 8, height: 8, borderRadius: '50%', background: theme.text }}/>
            <div style={{ width: 1, flex: 1, background: theme.border, margin: '4px 0', minHeight: 20 }}/>
            <div style={{ width: 8, height: 8, borderRadius: 2, background: accent }}/>
          </div>
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14 }}>
            <div>
              <div style={{ fontSize: 11, color: theme.textMuted, fontWeight: 600 }}>Recogerte en</div>
              <div style={{ fontSize: 13.5, fontWeight: 600, marginTop: 1 }}>Av. Salaverry 1850, Jesús María</div>
            </div>
            <div>
              <div style={{ fontSize: 11, color: theme.textMuted, fontWeight: 600 }}>Destino</div>
              <div style={{ fontSize: 13.5, fontWeight: 600, marginTop: 1 }}>UPC Monterrico</div>
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: 18, fontWeight: 800, letterSpacing: '-0.02em' }}>S/6</div>
            <div style={{ fontSize: 11, color: theme.textMuted }}>{t.perSeat}</div>
          </div>
        </div>

        {/* Safety actions */}
        <div style={{ display: 'flex', gap: 8, paddingTop: 8 }}>
          <ActionMini theme={theme} icon="shield" label={t.shareTrip}/>
          <ActionMini theme={theme} icon="users" label={t.trustedContacts}/>
          <ActionMini theme={theme} icon="close" label={t.cancel}/>
        </div>
      </div>
    </div>
  );
};

const ActionMini = ({ theme, icon, label }) => (
  <button style={{
    flex: 1, padding: '10px 8px', borderRadius: 12, background: theme.surfaceAlt, border: 'none',
    display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, cursor: 'pointer', fontFamily: 'inherit',
    color: theme.text,
  }}>
    <Icon name={icon} size={16}/>
    <span style={{ fontSize: 10.5, fontWeight: 600 }}>{label}</span>
  </button>
);

const ScreenPayment = ({ theme, t, accent, onNav }) => {
  const [method, setMethod] = React.useState('yape');
  const methods = [
    { id: 'yape', label: 'Yape', sub: '987 ••• 234', icon: 'phone', color: '#7C3AED' },
    { id: 'plin', label: 'Plin', sub: 'BCP · 987 ••• 234', icon: 'phone', color: '#0EA5E9' },
    { id: 'visa', label: 'Visa Crédito', sub: '•••• 4127', icon: 'card', color: theme.text },
    { id: 'cash', label: 'Efectivo al recoger', sub: 'Solo carpool', icon: 'wallet', color: theme.textMuted },
  ];
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '14px 20px 8px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => onNav('rent-detail')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
        <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>Confirmar pago</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: 4, color: theme.textMuted, fontSize: 12 }}>
          <Icon name="lock" size={12}/> Seguro
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 20px 0' }}>
        {/* Summary */}
        <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 18, padding: 16 }}>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', paddingBottom: 12, borderBottom: `1px solid ${theme.border}` }}>
            <div style={{ width: 56 }}>
              <CarPlaceholder theme={theme} height={56} label=""/>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14, fontWeight: 700 }}>Hyundai i10</div>
              <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 2 }}>9–11 may · 2 días</div>
            </div>
          </div>
          <div style={{ paddingTop: 12, display: 'flex', flexDirection: 'column', gap: 8 }}>
            <Row theme={theme} label="Alquiler · 2 días × S/89" value="S/ 178.00"/>
            <Row theme={theme} label="Servicio WheelsPe (5%)" value="S/ 8.90"/>
            <Row theme={theme} label="Garantía (escrow)" value="S/ 200.00" sub="Reembolsable"/>
            <div style={{ height: 1, background: theme.border, margin: '4px 0' }}/>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
              <span style={{ fontSize: 14, fontWeight: 700 }}>Total a pagar</span>
              <span style={{ fontSize: 22, fontWeight: 800, letterSpacing: '-0.02em' }}>S/ 386.90</span>
            </div>
          </div>
        </div>

        {/* Methods */}
        <div style={{ marginTop: 18 }}>
          <SectionHead theme={theme} title="Método de pago" action="Agregar" onAction={() => {}}/>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {methods.map(m => {
              const active = method === m.id;
              return (
                <button key={m.id} onClick={() => setMethod(m.id)} style={{
                  background: theme.surface, border: `1px solid ${active ? theme.text : theme.border}`,
                  borderRadius: 14, padding: 14, display: 'flex', alignItems: 'center', gap: 12,
                  cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left', color: theme.text,
                  position: 'relative',
                }}>
                  <div style={{ width: 36, height: 36, borderRadius: 10, background: m.color + '20', color: m.color, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Icon name={m.icon} size={16}/>
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 14, fontWeight: 700 }}>{m.label}</div>
                    <div style={{ fontSize: 11.5, color: theme.textMuted, marginTop: 1 }}>{m.sub}</div>
                  </div>
                  <div style={{ width: 20, height: 20, borderRadius: '50%', border: `2px solid ${active ? theme.text : theme.borderStrong}`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    {active && <div style={{ width: 10, height: 10, borderRadius: '50%', background: theme.text }}/>}
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        <div style={{ marginTop: 16, padding: 14, background: theme.accentSoft, borderRadius: 14, display: 'flex', gap: 12, alignItems: 'flex-start' }}>
          <Icon name="shield" size={18} color={accent}/>
          <div>
            <div style={{ fontSize: 13, fontWeight: 700 }}>Pago protegido</div>
            <div style={{ fontSize: 12, color: theme.textMuted, marginTop: 2, lineHeight: 1.5 }}>
              Tu garantía queda bloqueada en escrow y se libera al cierre del contrato sin observaciones.
            </div>
          </div>
        </div>
      </div>

      <div style={{ padding: 16, background: theme.surface, borderTop: `1px solid ${theme.border}` }}>
        <Btn kind="accent" full theme={theme} icon="lock" onClick={() => onNav('trip-active')}>Pagar S/ 386.90</Btn>
      </div>
    </div>
  );
};

const Row = ({ theme, label, value, sub }) => (
  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
    <span style={{ fontSize: 13, color: theme.textMuted }}>{label}</span>
    <div style={{ textAlign: 'right' }}>
      <div style={{ fontSize: 13.5, fontWeight: 600, color: theme.text }}>{value}</div>
      {sub && <div style={{ fontSize: 10.5, color: theme.textFaint, marginTop: 1 }}>{sub}</div>}
    </div>
  </div>
);

window.ScreenTripActive = ScreenTripActive;
window.ScreenPayment = ScreenPayment;
