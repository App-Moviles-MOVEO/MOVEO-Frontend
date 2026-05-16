// screens-social.jsx — Profile, Chat, Rewards

const ScreenProfile = ({ theme, t, accent, onNav }) => (
  <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
    <div style={{ padding: '14px 20px 8px', display: 'flex', alignItems: 'center', gap: 12 }}>
      <button onClick={() => onNav('home')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
      <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>{t.profile}</h1>
      <button style={iconBtn(theme)}><Icon name="settings" size={20}/></button>
    </div>

    <div style={{ flex: 1, overflowY: 'auto', padding: '8px 20px 20px' }}>
      {/* Profile header */}
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '12px 0 18px' }}>
        <Avatar name="Andrea" size={84} hue={220}/>
        <div style={{ marginTop: 12, display: 'flex', alignItems: 'center', gap: 6 }}>
          <h2 style={{ margin: 0, fontSize: 22, fontWeight: 800, letterSpacing: '-0.03em' }}>Andrea Pacheco</h2>
          <Icon name="check-circle" size={18} color={accent}/>
        </div>
        <div style={{ fontSize: 12.5, color: theme.textMuted, marginTop: 2 }}>Miembro desde feb 2025 · Lima</div>

        {/* Rep card */}
        <div style={{ marginTop: 16, width: '100%', background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 18, padding: 16 }}>
          <div style={{ display: 'flex', justifyContent: 'space-around' }}>
            <Stat theme={theme} label="Rating" value="4.9" accent={accent}/>
            <Stat theme={theme} label="Viajes" value="47"/>
            <Stat theme={theme} label="CO₂ ahorrado" value="38kg"/>
          </div>
          <div style={{ marginTop: 14, paddingTop: 14, borderTop: `1px solid ${theme.border}`, display: 'flex', gap: 8 }}>
            <Pill theme={theme}>✓ KYC verificado</Pill>
            <Pill theme={theme}>🎓 UPC</Pill>
            <Pill theme={theme}>💎 Nivel Plata</Pill>
          </div>
        </div>
      </div>

      {/* Latest reviews */}
      <SectionHead theme={theme} title={t.reviews} action="Ver todas" onAction={() => {}}/>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        {[
          { who: 'Diego A.', when: 'hace 2 días', stars: 5, text: 'Excelente pasajera, muy puntual y amable durante todo el trayecto.', hue: 200 },
          { who: 'Rosa M.', when: 'hace 1 sem', stars: 5, text: 'Devolvió el auto en perfectas condiciones. Recomendada al 100%.', hue: 30 },
          { who: 'Carolina V.', when: 'hace 2 sem', stars: 4, text: 'Buena comunicación, llegó al punto acordado a tiempo.', hue: 320 },
        ].map((r, i) => (
          <div key={i} style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 14, padding: 14 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <Avatar name={r.who} size={32} hue={r.hue}/>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 700 }}>{r.who}</div>
                <div style={{ fontSize: 10.5, color: theme.textFaint, marginTop: 1 }}>{r.when}</div>
              </div>
              <div style={{ display: 'flex', gap: 1 }}>
                {Array.from({length: 5}).map((_, k) => (
                  <Icon key={k} name="star" size={12} color={k < r.stars ? accent : theme.borderStrong}/>
                ))}
              </div>
            </div>
            <p style={{ margin: '8px 0 0', fontSize: 13, color: theme.textMuted, lineHeight: 1.5, textWrap: 'pretty' }}>"{r.text}"</p>
          </div>
        ))}
      </div>

      {/* Menu */}
      <div style={{ marginTop: 22, background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16, overflow: 'hidden' }}>
        <MenuRow theme={theme} icon="message" label={t.messages} badge="3" onClick={() => onNav('chat')} divider/>
        <MenuRow theme={theme} icon="gift" label={t.incentives} sub="450 puntos · Nivel Plata" onClick={() => onNav('rewards')} divider/>
        <MenuRow theme={theme} icon="shield" label="Seguridad" onClick={() => onNav('safety')} divider/>
        <MenuRow theme={theme} icon="card" label="Métodos de pago" onClick={() => onNav('payment')}/>
      </div>
    </div>
  </div>
);

const MenuRow = ({ theme, icon, label, sub, badge, onClick, divider }) => (
  <button onClick={onClick} style={{
    width: '100%', padding: 14, display: 'flex', alignItems: 'center', gap: 14,
    background: 'transparent', border: 'none', cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left',
    borderBottom: divider ? `1px solid ${theme.border}` : 'none', color: theme.text,
  }}>
    <Icon name={icon} size={18} color={theme.textMuted}/>
    <div style={{ flex: 1 }}>
      <div style={{ fontSize: 14, fontWeight: 600 }}>{label}</div>
      {sub && <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 1 }}>{sub}</div>}
    </div>
    {badge && <span style={{ minWidth: 20, height: 20, padding: '0 6px', borderRadius: 10, background: theme.accent, color: '#fff', fontSize: 11, fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{badge}</span>}
    <Icon name="chevron-right" size={16} color={theme.textFaint}/>
  </button>
);

const ScreenChat = ({ theme, t, accent, onNav }) => {
  const messages = [
    { from: 'them', text: 'Hola Andrea, soy Diego. Voy en camino al punto de encuentro 👋', time: '07:08' },
    { from: 'me', text: '¡Genial! Estoy en la esquina con Domingo Cueto.', time: '07:09' },
    { from: 'them', text: 'Perfecto, llego en 4 minutos. Toyota Yaris gris ABC-123.', time: '07:09' },
    { from: 'me', text: 'Gracias 🙏', time: '07:10' },
    { from: 'them', text: 'PIN del viaje: 4729 — te lo pediré al subir.', time: '07:10' },
  ];
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '12px 16px 12px', display: 'flex', alignItems: 'center', gap: 10, borderBottom: `1px solid ${theme.border}`, background: theme.surface }}>
        <button onClick={() => onNav('trip-active')} style={{ ...iconBtn(theme), background: 'transparent' }}><Icon name="arrow-left" size={20}/></button>
        <Avatar name="Diego" size={38} hue={200}/>
        <div style={{ flex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
            <span style={{ fontSize: 14, fontWeight: 700 }}>Diego A.</span>
            <Icon name="check-circle" size={12} color={accent}/>
          </div>
          <div style={{ fontSize: 11, color: theme.success, display: 'flex', alignItems: 'center', gap: 4 }}>
            <span style={{ width: 6, height: 6, borderRadius: '50%', background: theme.success }}/>
            En viaje · 12 min
          </div>
        </div>
        <button style={iconBtn(theme)}><Icon name="phone" size={18}/></button>
      </div>

      {/* Trip context bar */}
      <div style={{ padding: '8px 16px', background: theme.accentSoft, display: 'flex', alignItems: 'center', gap: 10 }}>
        <Icon name="route" size={14} color={accent}/>
        <span style={{ fontSize: 12, color: theme.text, fontWeight: 600 }}>Av. Salaverry → UPC Monterrico</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: 16, display: 'flex', flexDirection: 'column', gap: 8 }}>
        <div style={{ textAlign: 'center', fontSize: 10.5, color: theme.textFaint, padding: 8, fontWeight: 600, letterSpacing: '.04em' }}>HOY · 07:08</div>
        {messages.map((m, i) => (
          <div key={i} style={{ display: 'flex', justifyContent: m.from === 'me' ? 'flex-end' : 'flex-start' }}>
            <div style={{
              maxWidth: '78%', padding: '10px 14px',
              background: m.from === 'me' ? theme.text : theme.surface,
              color: m.from === 'me' ? theme.bg : theme.text,
              borderRadius: m.from === 'me' ? '16px 16px 4px 16px' : '16px 16px 16px 4px',
              border: m.from === 'me' ? 'none' : `1px solid ${theme.border}`,
            }}>
              <div style={{ fontSize: 13.5, lineHeight: 1.4 }}>{m.text}</div>
              <div style={{ fontSize: 10, opacity: 0.6, marginTop: 4, textAlign: 'right' }}>{m.time}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{ padding: 12, background: theme.surface, borderTop: `1px solid ${theme.border}`, display: 'flex', gap: 8, alignItems: 'center' }}>
        <div style={{ flex: 1, height: 44, background: theme.surfaceAlt, borderRadius: 22, display: 'flex', alignItems: 'center', padding: '0 16px', color: theme.textMuted, fontSize: 13 }}>
          Escribe un mensaje…
        </div>
        <button style={{ width: 44, height: 44, borderRadius: 22, background: accent, color: '#fff', border: 'none', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer' }}>
          <Icon name="arrow-right" size={18}/>
        </button>
      </div>
    </div>
  );
};

const ScreenRewards = ({ theme, t, accent, onNav }) => {
  const points = 450;
  const nextLevel = 750;
  const pct = Math.min(100, points / nextLevel * 100);
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '14px 20px 8px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => onNav('home')} style={iconBtn(theme)}><Icon name="arrow-left" size={20}/></button>
        <h1 style={{ flex: 1, margin: 0, fontSize: 20, fontWeight: 800, letterSpacing: '-0.02em' }}>{t.incentives}</h1>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 20px 20px' }}>
        {/* Level card */}
        <div style={{
          padding: 22, borderRadius: 22, position: 'relative', overflow: 'hidden',
          background: `linear-gradient(135deg, ${accent} 0%, oklch(0.50 0.15 240) 100%)`,
          color: '#fff',
        }}>
          <div style={{ position: 'absolute', right: -30, top: -30, width: 160, height: 160, borderRadius: '50%', background: 'rgba(255,255,255,.10)' }}/>
          <div style={{ position: 'absolute', right: 20, top: 60, width: 80, height: 80, borderRadius: '50%', background: 'rgba(255,255,255,.10)' }}/>
          <div style={{ position: 'relative' }}>
            <span style={{ fontSize: 11, fontWeight: 700, letterSpacing: '.06em', textTransform: 'uppercase', opacity: .85 }}>Nivel actual</span>
            <div style={{ marginTop: 4, display: 'flex', alignItems: 'baseline', gap: 6 }}>
              <span style={{ fontSize: 30, fontWeight: 800, letterSpacing: '-0.03em' }}>Plata</span>
              <span style={{ fontSize: 14, opacity: 0.7 }}>· {points} pts</span>
            </div>
            <div style={{ marginTop: 16 }}>
              <div style={{ height: 6, background: 'rgba(255,255,255,.20)', borderRadius: 3, overflow: 'hidden' }}>
                <div style={{ height: '100%', width: pct + '%', background: '#fff', borderRadius: 3 }}/>
              </div>
              <div style={{ marginTop: 8, fontSize: 11.5, opacity: 0.85 }}>Te faltan {nextLevel - points} pts para <strong>Oro</strong></div>
            </div>
          </div>
        </div>

        {/* How to earn */}
        <div style={{ marginTop: 18 }}>
          <SectionHead theme={theme} title="Cómo ganas puntos"/>
          <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 16, overflow: 'hidden' }}>
            <EarnRow theme={theme} accent={accent} icon="route" label="Cada viaje compartido" pts="+10" divider/>
            <EarnRow theme={theme} accent={accent} icon="car" label="Cada alquiler completado" pts="+25" divider/>
            <EarnRow theme={theme} accent={accent} icon="star" label="Reseña 5★ recibida" pts="+15" divider/>
            <EarnRow theme={theme} accent={accent} icon="users" label="Refiere a un amigo" pts="+50"/>
          </div>
        </div>

        {/* Rewards */}
        <div style={{ marginTop: 18 }}>
          <SectionHead theme={theme} title="Recompensas"/>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {[
              { title: 'S/ 5 off', sub: 'En tu próximo carpool', cost: 100, available: true },
              { title: '20% off', sub: 'Alquiler fin de semana', cost: 300, available: true },
              { title: 'SOAT regalo', sub: '6 meses', cost: 800, available: false },
              { title: 'Día premium', sub: 'Sin comisión', cost: 1200, available: false },
            ].map((r, i) => (
              <div key={i} style={{
                background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: 14, padding: 14,
                opacity: r.available ? 1 : 0.55,
              }}>
                <Icon name="gift" size={20} color={r.available ? accent : theme.textFaint}/>
                <div style={{ fontSize: 14, fontWeight: 800, marginTop: 8, letterSpacing: '-0.02em' }}>{r.title}</div>
                <div style={{ fontSize: 11, color: theme.textMuted, marginTop: 2 }}>{r.sub}</div>
                <div style={{ marginTop: 10, padding: '4px 8px', background: theme.surfaceAlt, borderRadius: 8, display: 'inline-block', fontSize: 11, fontWeight: 700 }}>
                  {r.cost} pts
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

const EarnRow = ({ theme, accent, icon, label, pts, divider }) => (
  <div style={{ padding: 14, display: 'flex', alignItems: 'center', gap: 12, borderBottom: divider ? `1px solid ${theme.border}` : 'none' }}>
    <div style={{ width: 32, height: 32, borderRadius: 10, background: theme.surfaceAlt, color: theme.text, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Icon name={icon} size={14}/>
    </div>
    <span style={{ flex: 1, fontSize: 13.5, fontWeight: 600 }}>{label}</span>
    <span style={{ fontSize: 14, fontWeight: 800, color: accent, letterSpacing: '-0.01em' }}>{pts}</span>
  </div>
);

window.ScreenProfile = ScreenProfile;
window.ScreenChat = ScreenChat;
window.ScreenRewards = ScreenRewards;
