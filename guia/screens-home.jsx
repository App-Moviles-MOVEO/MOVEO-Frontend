// screens-home.jsx — Home with role switcher (Pasajero/Conductor/Propietario)

const ScreenHome = ({ theme, t, accent, role, onNav, density }) => {
  const isOwner = role === 'propietario';
  const isDriver = role === 'conductor';

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      {/* Top bar */}
      <div style={{ padding: '14px 20px 10px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <Avatar name="Andrea" size={40} hue={220}/>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontSize: 12, color: theme.textMuted, fontWeight: 500 }}>{t.greeting},</span>
            <span style={{ fontSize: 16, fontWeight: 700, letterSpacing: '-0.02em' }}>Andrea P.</span>
          </div>
        </div>
        <div style={{ display: 'flex', gap: 6 }}>
          <button style={iconBtn(theme)}><Icon name="bell" size={20}/></button>
        </div>
      </div>

      {/* Hero card — varies by role */}
      <div style={{ padding: '6px 20px 0' }}>
        {isOwner ? (
          <div style={{
            borderRadius: 22, padding: 20, color: '#fff', position: 'relative', overflow: 'hidden',
            background: `linear-gradient(140deg, ${theme.text} 0%, ${theme.surfaceHi} 100%)`,
            border: `1px solid ${theme.borderStrong}`,
          }}>
            <span style={{ fontSize: 12, fontWeight: 600, opacity: 0.7, letterSpacing: '.06em', textTransform: 'uppercase', color: theme.bg }}>{t.earnings} · este mes</span>
            <div style={{ marginTop: 4, display: 'flex', alignItems: 'baseline', gap: 4, color: theme.bg }}>
              <span style={{ fontSize: 36, fontWeight: 800, letterSpacing: '-0.04em' }}>S/ 1,840</span>
              <span style={{ fontSize: 14, opacity: 0.6 }}>.50</span>
            </div>
            <div style={{ marginTop: 10, display: 'flex', gap: 8, color: theme.bg }}>
              <Pill theme={theme} dark><Icon name="trending" size={12}/> +18%</Pill>
              <Pill theme={theme} dark>3 alquileres activos</Pill>
            </div>
          </div>
        ) : (
          <div style={{
            borderRadius: 22, overflow: 'hidden', position: 'relative',
            background: theme.surface, border: `1px solid ${theme.border}`,
          }}>
            <div style={{ height: 140, position: 'relative' }}>
              <MapPlaceholder theme={theme} accent={accent}/>
            </div>
            <div style={{ padding: 16 }}>
              <h2 style={{ margin: 0, fontSize: 22, fontWeight: 800, letterSpacing: '-0.03em' }}>{t.where}</h2>
              <button onClick={() => onNav(isDriver ? 'carpool-publish' : 'carpool-search')} style={{
                marginTop: 12, width: '100%', height: 52, borderRadius: 14,
                background: theme.surfaceAlt, border: `1px solid ${theme.border}`,
                display: 'flex', alignItems: 'center', gap: 12, padding: '0 16px',
                color: theme.textMuted, fontFamily: 'inherit', fontSize: 14, cursor: 'pointer',
              }}>
                <Icon name="search" size={18}/>
                <span>Av. Javier Prado, San Isidro…</span>
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Quick actions */}
      <div style={{ padding: '20px 20px 8px' }}>
        <SectionHead theme={theme} title="Accesos rápidos"/>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {isOwner ? (
            <>
              <ActionTile theme={theme} accent={accent} icon="car" title={t.myCars} subtitle="3 activos" onClick={() => onNav('owner')}/>
              <ActionTile theme={theme} accent={accent} icon="plus" title="Publicar auto" subtitle="Nuevo listing" onClick={() => onNav('owner')}/>
              <ActionTile theme={theme} accent={accent} icon="wallet" title={t.earnings} subtitle="Ver detalle" onClick={() => onNav('payment')}/>
              <ActionTile theme={theme} accent={accent} icon="star-line" title={t.reviews} subtitle="4.9 ★" onClick={() => onNav('profile')}/>
            </>
          ) : (
            <>
              <ActionTile theme={theme} accent={accent} icon="car" title={t.rentCar} subtitle="Desde S/ 89/día" onClick={() => onNav('rent-list')}/>
              <ActionTile theme={theme} accent={accent} icon="route" title={t.findRoute} subtitle="Carpool a UPC" onClick={() => onNav('carpool-search')}/>
              <ActionTile theme={theme} accent={accent} icon="shield" title={t.trustedContacts} subtitle="3 personas" onClick={() => onNav('safety')}/>
              <ActionTile theme={theme} accent={accent} icon="gift" title={t.incentives} subtitle="450 puntos" onClick={() => onNav('rewards')}/>
            </>
          )}
        </div>
      </div>

      {/* Recommended */}
      <div style={{ padding: '12px 0 0', flex: 1 }}>
        <div style={{ padding: '0 20px' }}>
          <SectionHead theme={theme} title={isOwner ? 'Próximas reservas' : 'Cerca de ti'} action="Ver todo" onAction={() => onNav('rent-list')}/>
        </div>
        <div style={{ display: 'flex', gap: 12, padding: '0 20px 20px', overflowX: 'auto' }}>
          {isOwner ? (
            <>
              <BookingCard theme={theme} accent={accent} who="Carlos M." auto="Toyota Yaris 2022" date="vie 9 — dom 11 may" amount="S/ 360"/>
              <BookingCard theme={theme} accent={accent} who="Lucía R." auto="Hyundai Accent" date="lun 12 may" amount="S/ 95"/>
            </>
          ) : (
            <>
              <CarTile theme={theme} accent={accent} model="Hyundai i10" sub="Sedán · automático" price="89" rating="4.9" tag="Cerca"/>
              <CarTile theme={theme} accent={accent} model="Toyota Yaris" sub="Sedán · 2022" price="119" rating="4.8" tag="Top"/>
              <CarTile theme={theme} accent={accent} model="Kia Picanto" sub="Compacto" price="75" rating="4.7"/>
            </>
          )}
        </div>
      </div>

      <BottomNav active="home" theme={theme} t={t} onTab={(id) => onNav(id === 'home' ? 'home' : id === 'trips' ? 'trip-active' : id === 'wallet' ? 'payment' : 'profile')}/>
    </div>
  );
};

const iconBtn = (theme) => ({
  width: 40, height: 40, borderRadius: 12, background: theme.surfaceAlt, border: 'none',
  display: 'flex', alignItems: 'center', justifyContent: 'center', color: theme.text, cursor: 'pointer',
});

const Pill = ({ children, theme, dark }) => (
  <span style={{
    display: 'inline-flex', alignItems: 'center', gap: 4,
    padding: '4px 10px', borderRadius: 999,
    background: dark ? 'rgba(255,255,255,.10)' : theme.surfaceAlt,
    color: dark ? '#fff' : theme.textMuted,
    fontSize: 11, fontWeight: 600,
  }}>{children}</span>
);

const ActionTile = ({ theme, accent, icon, title, subtitle, onClick }) => (
  <button onClick={onClick} style={{
    background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16,
    padding: 14, textAlign: 'left', cursor: 'pointer', fontFamily: 'inherit',
    display: 'flex', flexDirection: 'column', gap: 10, color: theme.text,
  }}>
    <div style={{
      width: 36, height: 36, borderRadius: 10, background: theme.accentSoft, color: accent,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
    }}>
      <Icon name={icon} size={18}/>
    </div>
    <div>
      <div style={{ fontSize: 14, fontWeight: 700, letterSpacing: '-0.01em' }}>{title}</div>
      <div style={{ fontSize: 11.5, color: theme.textMuted, marginTop: 1 }}>{subtitle}</div>
    </div>
  </button>
);

const CarTile = ({ theme, accent, model, sub, price, rating, tag }) => (
  <div style={{ minWidth: 200, background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 18, padding: 12, display: 'flex', flexDirection: 'column', gap: 10 }}>
    <CarPlaceholder theme={theme} height={100} label={model.toLowerCase().replace(' ', '_') + '.jpg'}/>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
      <div>
        <div style={{ fontSize: 14, fontWeight: 700, color: theme.text, letterSpacing: '-0.01em' }}>{model}</div>
        <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 2 }}>{sub}</div>
      </div>
      {tag && <span style={{ fontSize: 10, padding: '3px 7px', background: accent, color: '#fff', borderRadius: 6, fontWeight: 700 }}>{tag}</span>}
    </div>
    <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', paddingTop: 4, borderTop: `1px solid ${theme.border}` }}>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 2 }}>
        <span style={{ fontSize: 16, fontWeight: 800, letterSpacing: '-0.02em' }}>S/{price}</span>
        <span style={{ fontSize: 11, color: theme.textMuted }}>/día</span>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 3, fontSize: 11, color: theme.textMuted }}>
        <Icon name="star" size={11} color={accent}/> {rating}
      </div>
    </div>
  </div>
);

const BookingCard = ({ theme, accent, who, auto, date, amount }) => (
  <div style={{ minWidth: 260, background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 18, padding: 14, display: 'flex', flexDirection: 'column', gap: 10 }}>
    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
      <Avatar name={who} size={36} hue={140}/>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 13, fontWeight: 700 }}>{who}</div>
        <div style={{ fontSize: 11, color: theme.textMuted }}>{auto}</div>
      </div>
    </div>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0', borderTop: `1px solid ${theme.border}` }}>
      <span style={{ fontSize: 12, color: theme.textMuted }}>{date}</span>
      <span style={{ fontSize: 14, fontWeight: 800, color: accent }}>{amount}</span>
    </div>
  </div>
);

window.ScreenHome = ScreenHome;
window.iconBtn = iconBtn;
window.Pill = Pill;
