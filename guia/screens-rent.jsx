// screens-rent.jsx — Rental list + detail + booking confirmation

const ScreenRentList = ({ theme, t, accent, onNav }) => {
  const [filter, setFilter] = React.useState('todos');
  const cars = [
    { id: 1, model: 'Hyundai i10', sub: 'Sedán · automático · 2023', price: 89, rating: 4.9, dist: '1.2 km', owner: 'Particular', verified: true, tag: 'Cerca' },
    { id: 2, model: 'Toyota Yaris', sub: 'Sedán · automático · 2022', price: 119, rating: 4.8, dist: '2.4 km', owner: 'AutoLima Rent', verified: true, tag: 'Top' },
    { id: 3, model: 'Kia Picanto', sub: 'Compacto · mecánico · 2021', price: 75, rating: 4.7, dist: '3.1 km', owner: 'Particular', verified: true },
    { id: 4, model: 'Suzuki Swift', sub: 'Compacto · automático', price: 95, rating: 4.6, dist: '4.5 km', owner: 'Particular', verified: false },
  ];
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '14px 20px 10px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => onNav('home')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
        <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>{t.rentCar}</h1>
        <button style={iconBtn(theme)}><Icon name="filter" size={20}/></button>
      </div>

      {/* Search bar */}
      <div style={{ padding: '0 20px 12px' }}>
        <div style={{ display: 'flex', gap: 8 }}>
          <div style={{ flex: 1, height: 46, borderRadius: 14, background: theme.surface, border: `1px solid ${theme.border}`, display: 'flex', alignItems: 'center', gap: 10, padding: '0 14px' }}>
            <Icon name="search" size={16} color={theme.textMuted}/>
            <span style={{ fontSize: 13, color: theme.textMuted }}>San Isidro · Lima</span>
          </div>
          <button style={{ height: 46, padding: '0 14px', borderRadius: 14, background: theme.surface, border: `1px solid ${theme.border}`, display: 'flex', alignItems: 'center', gap: 6, fontFamily: 'inherit', fontSize: 13, color: theme.text, cursor: 'pointer' }}>
            <Icon name="calendar" size={14}/>
            9–11 may
          </button>
        </div>
      </div>

      {/* Filter chips */}
      <div style={{ padding: '0 0 12px' }}>
        <div style={{ display: 'flex', gap: 8, padding: '0 20px', overflowX: 'auto' }}>
          {['todos', 'compactos', 'sedán', 'SUV', 'automático', 'eléctrico'].map(f => (
            <Chip key={f} active={filter === f} theme={theme} onClick={() => setFilter(f)}>{f}</Chip>
          ))}
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', padding: '4px 2px' }}>
          <span style={{ fontSize: 13, color: theme.textMuted }}>{cars.length} autos disponibles</span>
          <button style={{ background: 'none', border: 'none', color: theme.text, fontFamily: 'inherit', fontSize: 13, fontWeight: 600, display: 'flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}>
            Más cercano <Icon name="chevron-down" size={14}/>
          </button>
        </div>
        {cars.map(c => (
          <button key={c.id} onClick={() => onNav('rent-detail')} style={{
            background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 18,
            padding: 12, display: 'flex', gap: 12, cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left', color: theme.text,
          }}>
            <div style={{ width: 100, flexShrink: 0 }}>
              <CarPlaceholder theme={theme} height={100} label="" />
            </div>
            <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <span style={{ fontSize: 15, fontWeight: 700, letterSpacing: '-0.02em' }}>{c.model}</span>
                  {c.verified && <Icon name="check-circle" size={14} color={accent}/>}
                </div>
                <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 2 }}>{c.sub}</div>
                <div style={{ display: 'flex', gap: 10, marginTop: 6, fontSize: 11, color: theme.textMuted }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: 3 }}><Icon name="star" size={11} color={accent}/> {c.rating}</span>
                  <span>·</span>
                  <span>{c.dist}</span>
                  <span>·</span>
                  <span>{c.owner}</span>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 2, marginTop: 6 }}>
                <span style={{ fontSize: 17, fontWeight: 800, letterSpacing: '-0.02em' }}>S/{c.price}</span>
                <span style={{ fontSize: 11, color: theme.textMuted }}>/día</span>
              </div>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
};

const ScreenRentDetail = ({ theme, t, accent, onNav }) => (
  <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
    <div style={{ position: 'relative' }}>
      <CarPlaceholder theme={theme} height={240} label="hyundai_i10_2023.jpg"/>
      <button onClick={() => onNav('rent-list')} style={{ ...iconBtn(theme), position: 'absolute', top: 14, left: 14, background: theme.surface }}>
        <Icon name="arrow-left" size={20}/>
      </button>
      <div style={{ position: 'absolute', top: 14, right: 14, display: 'flex', gap: 6 }}>
        <button style={{ ...iconBtn(theme), background: theme.surface }}><Icon name="star-line" size={20}/></button>
      </div>
      <div style={{ position: 'absolute', bottom: 12, left: 14, display: 'flex', gap: 6 }}>
        {[0,1,2,3].map(i => <div key={i} style={{ width: i === 0 ? 18 : 6, height: 6, borderRadius: 3, background: i === 0 ? accent : 'rgba(255,255,255,0.6)' }}/>)}
      </div>
    </div>

    <div style={{ flex: 1, overflowY: 'auto', padding: '20px 20px 0' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12 }}>
        <div>
          <h1 style={{ margin: 0, fontSize: 24, fontWeight: 800, letterSpacing: '-0.03em' }}>Hyundai i10</h1>
          <div style={{ marginTop: 4, fontSize: 13, color: theme.textMuted }}>Sedán · automático · 2023</div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 1, justifyContent: 'flex-end' }}>
            <span style={{ fontSize: 24, fontWeight: 800, letterSpacing: '-0.03em' }}>S/89</span>
            <span style={{ fontSize: 12, color: theme.textMuted }}>/día</span>
          </div>
          <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 2 }}>+ S/200 garantía</div>
        </div>
      </div>

      {/* Spec row */}
      <div style={{ marginTop: 18, padding: '14px 0', borderTop: `1px solid ${theme.border}`, borderBottom: `1px solid ${theme.border}`, display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
        <SpecCell theme={theme} icon="users" label="5 plazas"/>
        <SpecCell theme={theme} icon="fuel" label="Gasoil"/>
        <SpecCell theme={theme} icon="settings" label="Auto"/>
        <SpecCell theme={theme} icon="leaf" label="A/C"/>
      </div>

      {/* Owner */}
      <div style={{ marginTop: 16, display: 'flex', alignItems: 'center', gap: 12 }}>
        <Avatar name="Rosa" size={44} hue={20}/>
        <div style={{ flex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ fontSize: 14, fontWeight: 700 }}>Rosa M.</span>
            <Icon name="check-circle" size={14} color={accent}/>
          </div>
          <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 1 }}>Propietaria · 4.9 ★ · 38 reservas</div>
        </div>
        <button style={iconBtn(theme)} onClick={() => onNav('chat')}><Icon name="message" size={18}/></button>
      </div>

      <div style={{ marginTop: 18 }}>
        <h3 style={{ margin: '0 0 8px', fontSize: 15, fontWeight: 700, letterSpacing: '-0.02em' }}>Sobre este auto</h3>
        <p style={{ margin: 0, fontSize: 13.5, color: theme.textMuted, lineHeight: 1.55, textWrap: 'pretty' }}>
          Auto particular en perfecto estado, ideal para la ciudad. Incluye SOAT vigente, kit de emergencia y revisión técnica al día.
        </p>
      </div>

      <div style={{ marginTop: 18, padding: 14, background: theme.accentSoft, borderRadius: 14, display: 'flex', gap: 12, alignItems: 'flex-start' }}>
        <div style={{ width: 32, height: 32, borderRadius: 10, background: accent, color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
          <Icon name="shield" size={16}/>
        </div>
        <div>
          <div style={{ fontSize: 13, fontWeight: 700, color: theme.text }}>Garantía retenida en escrow</div>
          <div style={{ fontSize: 12, color: theme.textMuted, marginTop: 2, lineHeight: 1.4 }}>Tu dinero queda bloqueado y se libera al devolver el auto sin observaciones.</div>
        </div>
      </div>
    </div>

    {/* Sticky CTA */}
    <div style={{ padding: 16, background: theme.surface, borderTop: `1px solid ${theme.border}`, display: 'flex', gap: 10, alignItems: 'center' }}>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 11, color: theme.textMuted, fontWeight: 600 }}>Total · 2 días</div>
        <div style={{ fontSize: 18, fontWeight: 800, letterSpacing: '-0.02em' }}>S/ 178 + S/200</div>
      </div>
      <Btn kind="accent" theme={theme} onClick={() => onNav('payment')}>{t.book}</Btn>
    </div>
  </div>
);

const SpecCell = ({ theme, icon, label }) => (
  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5 }}>
    <Icon name={icon} size={18} color={theme.textMuted}/>
    <span style={{ fontSize: 11, color: theme.text, fontWeight: 600 }}>{label}</span>
  </div>
);

window.ScreenRentList = ScreenRentList;
window.ScreenRentDetail = ScreenRentDetail;
