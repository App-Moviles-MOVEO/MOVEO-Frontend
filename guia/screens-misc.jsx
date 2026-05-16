// screens-misc.jsx — Owner, Safety, Profile, Chat, Rewards

const ScreenOwner = ({ theme, t, accent, onNav }) => (
  <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
    <div style={{ padding: '14px 20px 8px', display: 'flex', alignItems: 'center', gap: 12 }}>
      <button onClick={() => onNav('home')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
      <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>{t.myCars}</h1>
      <button style={iconBtn(theme)}><Icon name="plus" size={20}/></button>
    </div>

    <div style={{ flex: 1, overflowY: 'auto', padding: '8px 20px 20px' }}>
      {/* Earnings card */}
      <div style={{
        borderRadius: 22, padding: 18, color: theme.bg,
        background: `linear-gradient(140deg, ${theme.text}, ${theme.surfaceHi})`,
        border: `1px solid ${theme.borderStrong}`,
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 11, fontWeight: 700, letterSpacing: '.06em', textTransform: 'uppercase', opacity: .7 }}>Ganancias · Mayo</span>
          <Icon name="trending" size={16}/>
        </div>
        <div style={{ marginTop: 6, fontSize: 36, fontWeight: 800, letterSpacing: '-0.04em' }}>S/ 1,840.50</div>
        <div style={{ marginTop: 12, display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12 }}>
          <Mini label="Reservas" value="12"/>
          <Mini label="Ocupación" value="68%"/>
          <Mini label="Rating" value="4.9 ★"/>
        </div>
      </div>

      {/* Calendar mini */}
      <div style={{ marginTop: 18 }}>
        <SectionHead theme={theme} title="Disponibilidad · Mayo" action="Editar" onAction={() => {}}/>
        <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16, padding: 14 }}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 6, fontSize: 10, color: theme.textFaint, fontWeight: 700, textAlign: 'center' }}>
            {['L','M','M','J','V','S','D'].map((d, i) => <div key={i}>{d}</div>)}
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 6, marginTop: 6 }}>
            {Array.from({length: 28}).map((_, i) => {
              const day = i + 1;
              const booked = [9, 10, 11, 16, 17, 23].includes(day);
              const today = day === 6;
              return (
                <div key={i} style={{
                  aspectRatio: '1', borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: 11.5, fontWeight: 600,
                  background: today ? theme.text : booked ? accent : theme.surfaceAlt,
                  color: today ? theme.bg : booked ? '#fff' : theme.textMuted,
                }}>{day}</div>
              );
            })}
          </div>
          <div style={{ marginTop: 12, display: 'flex', gap: 14, fontSize: 11 }}>
            <Legend swatch={accent} label="Reservado" theme={theme}/>
            <Legend swatch={theme.surfaceAlt} label="Disponible" theme={theme}/>
          </div>
        </div>
      </div>

      {/* My cars */}
      <div style={{ marginTop: 18 }}>
        <SectionHead theme={theme} title="Mi flota"/>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {[
            { model: 'Hyundai i10', plate: 'ABC-123', status: 'En alquiler · vuelve dom', stat: 'live', earn: '178' },
            { model: 'Kia Picanto', plate: 'BFG-417', status: 'Disponible', stat: 'idle', earn: '0' },
            { model: 'Toyota Yaris', plate: 'DLP-882', status: 'Mantenimiento', stat: 'maint', earn: '0' },
          ].map(car => (
            <div key={car.plate} style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16, padding: 12, display: 'flex', gap: 12, alignItems: 'center' }}>
              <div style={{ width: 80 }}>
                <CarPlaceholder theme={theme} height={70} label=""/>
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 14, fontWeight: 700 }}>{car.model}</div>
                <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 2, fontFamily: 'JetBrains Mono, monospace' }}>{car.plate}</div>
                <div style={{ marginTop: 6, display: 'flex', alignItems: 'center', gap: 6 }}>
                  <span style={{ width: 6, height: 6, borderRadius: '50%', background: car.stat === 'live' ? theme.success : car.stat === 'idle' ? theme.textFaint : theme.warn }}/>
                  <span style={{ fontSize: 11, color: theme.textMuted }}>{car.status}</span>
                </div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: 11, color: theme.textFaint }}>Mes</div>
                <div style={{ fontSize: 14, fontWeight: 800, letterSpacing: '-0.02em' }}>S/ {car.earn}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  </div>
);

const Mini = ({ label, value }) => (
  <div>
    <div style={{ fontSize: 10, opacity: 0.6, fontWeight: 600, letterSpacing: '.04em', textTransform: 'uppercase' }}>{label}</div>
    <div style={{ fontSize: 18, fontWeight: 800, marginTop: 2, letterSpacing: '-0.02em' }}>{value}</div>
  </div>
);

const Legend = ({ swatch, label, theme }) => (
  <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
    <span style={{ width: 12, height: 12, borderRadius: 4, background: swatch }}/>
    <span style={{ color: theme.textMuted, fontSize: 11 }}>{label}</span>
  </div>
);

const ScreenSafety = ({ theme, t, accent, onNav }) => {
  const [onlyWomen, setOnlyWomen] = React.useState(true);
  const [shareLive, setShareLive] = React.useState(true);
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '14px 20px 8px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => onNav('home')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
        <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>Seguridad</h1>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 20px 20px' }}>
        {/* Hero */}
        <div style={{
          padding: 20, borderRadius: 20, background: theme.accentSoft, border: `1px solid ${theme.border}`,
          display: 'flex', gap: 14, alignItems: 'center',
        }}>
          <div style={{ width: 48, height: 48, borderRadius: 14, background: accent, color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
            <Icon name="shield" size={24}/>
          </div>
          <div>
            <div style={{ fontSize: 15, fontWeight: 700 }}>Tu seguridad primero</div>
            <div style={{ fontSize: 12.5, color: theme.textMuted, marginTop: 2, lineHeight: 1.45 }}>Configura quién recibe tu ubicación y qué viajes aceptas.</div>
          </div>
        </div>

        {/* Toggles */}
        <div style={{ marginTop: 18 }}>
          <SectionHead theme={theme} title="Preferencias"/>
          <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16, overflow: 'hidden' }}>
            <ToggleRow theme={theme} accent={accent} icon="female" title={t.onlyWomen} sub="Solo verás conductoras y pasajeras" on={onlyWomen} onClick={() => setOnlyWomen(!onlyWomen)} divider/>
            <ToggleRow theme={theme} accent={accent} icon="pin" title="Compartir viaje en vivo" sub="Tus contactos verán tu ubicación durante el viaje" on={shareLive} onClick={() => setShareLive(!shareLive)} divider/>
            <ToggleRow theme={theme} accent={accent} icon="check" title="Solo verificados" sub="Conductores con KYC + antigüedad ≥ 30 días" on={true}/>
          </div>
        </div>

        {/* Trusted contacts */}
        <div style={{ marginTop: 18 }}>
          <SectionHead theme={theme} title={t.trustedContacts} action="Agregar" onAction={() => {}}/>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {[
              { name: 'Mamá', phone: '+51 987 234 567', hue: 340 },
              { name: 'Sofía R.', phone: '+51 991 100 245', hue: 200 },
              { name: 'Universidad UPC', phone: 'Línea 24/7', hue: 30, badge: true },
            ].map(c => (
              <div key={c.name} style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 14, padding: 12, display: 'flex', alignItems: 'center', gap: 12 }}>
                <Avatar name={c.name} size={40} hue={c.hue}/>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                    <span style={{ fontSize: 14, fontWeight: 700 }}>{c.name}</span>
                    {c.badge && <Icon name="check-circle" size={12} color={accent}/>}
                  </div>
                  <div style={{ fontSize: 11.5, color: theme.textMuted, marginTop: 1 }}>{c.phone}</div>
                </div>
                <button style={iconBtn(theme)}><Icon name="phone" size={16}/></button>
              </div>
            ))}
          </div>
        </div>

        {/* Emergency */}
        <div style={{ marginTop: 18 }}>
          <SectionHead theme={theme} title="En caso de emergencia"/>
          <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16, padding: 16, display: 'flex', flexDirection: 'column', gap: 12 }}>
            <div style={{ display: 'flex', gap: 8 }}>
              <EmergencyBtn theme={theme} label="Policía" num="105" color={theme.danger}/>
              <EmergencyBtn theme={theme} label="Bomberos" num="116" color={theme.warn}/>
              <EmergencyBtn theme={theme} label="Serenazgo" num="-"  color={theme.textMuted}/>
            </div>
            <div style={{ fontSize: 11.5, color: theme.textMuted, lineHeight: 1.5 }}>
              Al pulsar SOS durante un viaje, enviamos tu ubicación, datos del conductor y placa a tus contactos de confianza y a la línea correspondiente.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const ToggleRow = ({ theme, accent, icon, title, sub, on, onClick, divider }) => (
  <div onClick={onClick} style={{
    padding: 14, display: 'flex', alignItems: 'center', gap: 12, cursor: 'pointer',
    borderBottom: divider ? `1px solid ${theme.border}` : 'none',
  }}>
    <div style={{ width: 36, height: 36, borderRadius: 10, background: theme.accentSoft, color: accent, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
      <Icon name={icon} size={16}/>
    </div>
    <div style={{ flex: 1 }}>
      <div style={{ fontSize: 14, fontWeight: 700, color: theme.text }}>{title}</div>
      <div style={{ fontSize: 11.5, color: theme.textMuted, marginTop: 2 }}>{sub}</div>
    </div>
    <Toggle on={on} accent={accent} theme={theme}/>
  </div>
);

const EmergencyBtn = ({ theme, label, num, color }) => (
  <button style={{
    flex: 1, padding: '12px 6px', borderRadius: 12, background: theme.surfaceAlt, border: 'none',
    display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, cursor: 'pointer', fontFamily: 'inherit',
  }}>
    <span style={{ fontSize: 18, fontWeight: 800, color, letterSpacing: '-0.02em' }}>{num}</span>
    <span style={{ fontSize: 10.5, color: theme.text, fontWeight: 600 }}>{label}</span>
  </button>
);

window.ScreenOwner = ScreenOwner;
window.ScreenSafety = ScreenSafety;
