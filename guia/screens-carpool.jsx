// screens-carpool.jsx — Carpool search + publish + trip active

const ScreenCarpoolSearch = ({ theme, t, accent, onNav }) => {
  const [onlyWomen, setOnlyWomen] = React.useState(false);
  const trips = [
    { id: 1, driver: 'Carolina V.', from: 'Av. Salaverry', to: 'UPC Monterrico', time: '07:15', seats: 2, price: 6, rating: 4.9, hue: 320, verified: true, women: true, badge: 'UPC' },
    { id: 2, driver: 'Diego A.', from: 'Pueblo Libre', to: 'UPC Monterrico', time: '07:30', seats: 3, price: 5, rating: 4.8, hue: 200, verified: true, badge: 'UPC' },
    { id: 3, driver: 'Lucía P.', from: 'Magdalena', to: 'San Isidro Centro', time: '08:00', seats: 1, price: 7, rating: 5.0, hue: 280, verified: true, women: true },
  ];
  const filtered = onlyWomen ? trips.filter(t => t.women) : trips;
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '14px 20px 8px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => onNav('home')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
        <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>{t.findRoute}</h1>
      </div>

      {/* Origin → destination card */}
      <div style={{ padding: '4px 20px 14px' }}>
        <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16, padding: 14 }}>
          <div style={{ display: 'flex', gap: 12 }}>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', paddingTop: 6 }}>
              <div style={{ width: 8, height: 8, borderRadius: '50%', background: theme.text }}/>
              <div style={{ width: 1, flex: 1, background: theme.border, margin: '4px 0' }}/>
              <div style={{ width: 8, height: 8, borderRadius: 2, background: accent }}/>
            </div>
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 8 }}>
              <div>
                <div style={{ fontSize: 11, color: theme.textMuted, fontWeight: 600, letterSpacing: '.04em', textTransform: 'uppercase' }}>Desde</div>
                <div style={{ fontSize: 14, fontWeight: 600, marginTop: 2 }}>Av. Salaverry, Jesús María</div>
              </div>
              <div style={{ height: 1, background: theme.border }}/>
              <div>
                <div style={{ fontSize: 11, color: theme.textMuted, fontWeight: 600, letterSpacing: '.04em', textTransform: 'uppercase' }}>Hasta</div>
                <div style={{ fontSize: 14, fontWeight: 600, marginTop: 2 }}>UPC Monterrico</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Filters */}
      <div style={{ padding: '0 0 12px' }}>
        <div style={{ display: 'flex', gap: 8, padding: '0 20px', overflowX: 'auto' }}>
          <Chip theme={theme} icon="calendar" active>Hoy 7:00</Chip>
          <Chip theme={theme} icon="seat">1 asiento</Chip>
          <Chip theme={theme} icon="female" active={onlyWomen} onClick={() => setOnlyWomen(!onlyWomen)}>{t.onlyWomen}</Chip>
          <Chip theme={theme} icon="check">Verificado UPC</Chip>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div style={{ fontSize: 13, color: theme.textMuted, padding: '4px 2px' }}>{filtered.length} rutas disponibles</div>
        {filtered.map(trip => (
          <button key={trip.id} onClick={() => onNav('trip-active')} style={{
            background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 18,
            padding: 14, cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left', color: theme.text,
            display: 'flex', flexDirection: 'column', gap: 12,
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <Avatar name={trip.driver} size={42} hue={trip.hue}/>
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <span style={{ fontSize: 14, fontWeight: 700 }}>{trip.driver}</span>
                  {trip.verified && <Icon name="check-circle" size={13} color={accent}/>}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginTop: 2 }}>
                  <span style={{ fontSize: 11, display: 'flex', alignItems: 'center', gap: 3, color: theme.textMuted }}><Icon name="star" size={10} color={accent}/> {trip.rating}</span>
                  {trip.badge && <span style={{ fontSize: 10, padding: '1px 6px', borderRadius: 4, background: theme.surfaceAlt, fontWeight: 700, color: theme.text }}>✓ {trip.badge}</span>}
                  {trip.women && <Icon name="female" size={11} color={accent}/>}
                </div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: 17, fontWeight: 800, letterSpacing: '-0.02em' }}>S/{trip.price}</div>
                <div style={{ fontSize: 10, color: theme.textMuted }}>{t.perSeat}</div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '10px 0', borderTop: `1px solid ${theme.border}` }}>
              <div style={{ fontSize: 18, fontWeight: 800, letterSpacing: '-0.02em' }}>{trip.time}</div>
              <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 4 }}>
                <div style={{ fontSize: 12, color: theme.text }}>{trip.from}</div>
                <div style={{ height: 1, background: theme.border, position: 'relative' }}>
                  <div style={{ position: 'absolute', top: -3, left: 0, width: 6, height: 6, borderRadius: '50%', background: theme.text }}/>
                  <div style={{ position: 'absolute', top: -3, right: 0, width: 6, height: 6, borderRadius: 2, background: accent }}/>
                </div>
                <div style={{ fontSize: 12, color: theme.text }}>{trip.to}</div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 11, color: theme.textMuted }}>
                <Icon name="seat" size={13}/> {trip.seats}
              </div>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
};

const ScreenCarpoolPublish = ({ theme, t, accent, onNav }) => {
  const [seats, setSeats] = React.useState(2);
  const [recurring, setRecurring] = React.useState(true);
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '14px 20px 4px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => onNav('home')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
        <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>{t.publishRoute}</h1>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 20px 0', display: 'flex', flexDirection: 'column', gap: 14 }}>
        <FieldGroup theme={theme} label="Origen y destino">
          <FieldRow theme={theme} icon="pin-fill" iconColor={theme.text} placeholder="Tu punto de partida" value="Surco · Av. Caminos del Inca 312"/>
          <FieldRow theme={theme} icon="pin-fill" iconColor={accent} placeholder="A dónde vas" value="UPC Monterrico"/>
        </FieldGroup>

        <FieldGroup theme={theme} label="Cuándo">
          <div style={{ display: 'flex', gap: 8 }}>
            <Field theme={theme} icon="calendar" value="Vie 9 may"/>
            <Field theme={theme} icon="clock" value="07:15"/>
          </div>
          <button onClick={() => setRecurring(!recurring)} style={{
            marginTop: 8, padding: '12px 14px', display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            background: 'transparent', border: `1px solid ${theme.border}`, borderRadius: 14, cursor: 'pointer', fontFamily: 'inherit',
            color: theme.text,
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <Icon name="route" size={18} color={theme.textMuted}/>
              <div style={{ textAlign: 'left' }}>
                <div style={{ fontSize: 13.5, fontWeight: 600 }}>Repetir L–V</div>
                <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 1 }}>Tu ruta se publicará cada día hábil</div>
              </div>
            </div>
            <Toggle on={recurring} accent={accent} theme={theme}/>
          </button>
        </FieldGroup>

        <FieldGroup theme={theme} label="Asientos disponibles">
          <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 14, padding: 14, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <Icon name="seat" size={18} color={theme.textMuted}/>
              <span style={{ fontSize: 14, fontWeight: 600 }}>{seats} {t.seats}</span>
            </div>
            <div style={{ display: 'flex', gap: 6 }}>
              <button onClick={() => setSeats(Math.max(1, seats - 1))} style={stepBtn(theme)}>−</button>
              <button onClick={() => setSeats(Math.min(4, seats + 1))} style={stepBtn(theme)}>+</button>
            </div>
          </div>
        </FieldGroup>

        <FieldGroup theme={theme} label="Tarifa por asiento">
          <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 14, padding: 14, display: 'flex', alignItems: 'center', gap: 14 }}>
            <span style={{ fontSize: 26, fontWeight: 800, letterSpacing: '-0.03em' }}>S/ 6.00</span>
            <span style={{ fontSize: 12, color: theme.textMuted }}>Sugerido por trayecto</span>
          </div>
          <div style={{ marginTop: 8, padding: 12, background: theme.surfaceAlt, borderRadius: 12, display: 'flex', alignItems: 'center', gap: 10 }}>
            <Icon name="sparkle" size={14} color={accent}/>
            <span style={{ fontSize: 12, color: theme.textMuted }}>Si llenas todos los asientos, recibes <strong style={{ color: theme.text }}>S/ {seats * 6}</strong> por viaje.</span>
          </div>
        </FieldGroup>
      </div>

      <div style={{ padding: 16, background: theme.surface, borderTop: `1px solid ${theme.border}` }}>
        <Btn kind="accent" full theme={theme} onClick={() => onNav('home')}>Publicar ruta</Btn>
      </div>
    </div>
  );
};

const Toggle = ({ on, accent, theme }) => (
  <div style={{ width: 40, height: 24, borderRadius: 12, background: on ? accent : theme.surfaceHi, position: 'relative', transition: 'background .18s' }}>
    <div style={{ position: 'absolute', top: 2, left: on ? 18 : 2, width: 20, height: 20, borderRadius: '50%', background: '#fff', boxShadow: '0 1px 3px rgba(0,0,0,.2)', transition: 'left .18s' }}/>
  </div>
);

const stepBtn = (theme) => ({
  width: 32, height: 32, borderRadius: 10, background: theme.surfaceAlt, border: 'none',
  fontFamily: 'inherit', fontSize: 16, fontWeight: 700, color: theme.text, cursor: 'pointer',
});

const FieldGroup = ({ theme, label, children }) => (
  <div>
    <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: '.06em', textTransform: 'uppercase', color: theme.textFaint, padding: '0 4px 8px' }}>{label}</div>
    <div style={{ display: 'flex', flexDirection: 'column', gap: 0 }}>
      {children}
    </div>
  </div>
);

const FieldRow = ({ theme, icon, iconColor, value }) => (
  <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 14, padding: '14px 14px', display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
    <Icon name={icon} size={18} color={iconColor}/>
    <span style={{ fontSize: 14, fontWeight: 600, color: theme.text }}>{value}</span>
  </div>
);

const Field = ({ theme, icon, value }) => (
  <div style={{ flex: 1, background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 14, padding: '14px', display: 'flex', alignItems: 'center', gap: 10 }}>
    <Icon name={icon} size={16} color={theme.textMuted}/>
    <span style={{ fontSize: 13.5, fontWeight: 600 }}>{value}</span>
  </div>
);

window.ScreenCarpoolSearch = ScreenCarpoolSearch;
window.ScreenCarpoolPublish = ScreenCarpoolPublish;
