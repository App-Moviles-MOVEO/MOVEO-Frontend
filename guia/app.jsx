// app.jsx — Main composition: Tweaks + role/theme + canvas of all screens

const { useState, useEffect, useMemo } = React;

// Phone wrapper — Android-ish frame, content fills
const Phone = ({ children, theme, density, scale = 1 }) => {
  const w = 360, h = 740;
  return (
    <div style={{
      width: w, height: h, borderRadius: 38,
      background: theme.bg, color: theme.text,
      border: `10px solid ${theme.mode === 'dark' ? '#0A0A0A' : '#1A1F28'}`,
      boxShadow: theme.mode === 'dark'
        ? '0 30px 80px rgba(0,0,0,0.6), 0 0 0 2px rgba(255,255,255,0.04) inset'
        : '0 30px 80px rgba(10,14,20,0.20)',
      overflow: 'hidden', position: 'relative',
      fontFamily: 'Manrope, system-ui, sans-serif',
      fontSize: 14 * (density === 'compacta' ? 0.92 : density === 'espaciosa' ? 1.06 : 1),
      transform: `scale(${scale})`, transformOrigin: 'top left',
    }}>
      {/* Status bar */}
      <div style={{
        position: 'absolute', top: 0, left: 0, right: 0, height: 32,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '0 22px', fontSize: 12, fontWeight: 700, color: theme.text,
        zIndex: 10, pointerEvents: 'none', letterSpacing: '-0.01em',
      }}>
        <span>9:41</span>
        <div style={{ position: 'absolute', left: '50%', top: 8, transform: 'translateX(-50%)', width: 90, height: 22, borderRadius: 999, background: theme.mode === 'dark' ? '#000' : '#1A1F28' }}/>
        <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          <svg width="14" height="10" viewBox="0 0 14 10" fill="currentColor"><path d="M1 7h2v3H1zm4-2h2v5H5zm4-2h2v7H9zm4-2h1v9h-1z"/></svg>
          <svg width="14" height="10" viewBox="0 0 14 10" fill="currentColor"><path d="M7 2C4 2 2 4 1 5l1 1c1-1 3-3 5-3s4 2 5 3l1-1c-1-1-3-3-6-3zm0 3C5 5 4 6 3 7l1 1c1-1 1-2 3-2s2 1 3 2l1-1c-1-1-2-2-4-2zm0 3l-1 2h2L7 8z"/></svg>
          <svg width="22" height="10" viewBox="0 0 22 10" fill="none"><rect x="1" y="1" width="18" height="8" rx="2" stroke="currentColor" strokeWidth="1"/><rect x="3" y="3" width="14" height="4" rx="1" fill="currentColor"/><rect x="20" y="4" width="1.5" height="3" rx="0.5" fill="currentColor"/></svg>
        </div>
      </div>
      {/* Content area */}
      <div style={{ position: 'absolute', inset: 0, paddingTop: 32, display: 'flex', flexDirection: 'column' }}>
        <div style={{ flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>{children}</div>
      </div>
      {/* Home indicator */}
      <div style={{
        position: 'absolute', bottom: 6, left: '50%', transform: 'translateX(-50%)',
        width: 110, height: 4, borderRadius: 2, background: theme.text, opacity: 0.35, zIndex: 11,
      }}/>
    </div>
  );
};

// Role switcher rendering — pick which top "tab" each artboard represents
const ALL_SCREENS = [
  { id: 'onboarding',     label: '01 · Bienvenida',       comp: 'ScreenOnboarding' },
  { id: 'kyc',            label: '02 · KYC con DNI',      comp: 'ScreenKyc' },
  { id: 'home',           label: '03 · Inicio',           comp: 'ScreenHome' },
  { id: 'rent-list',      label: '04 · Alquilar (lista)', comp: 'ScreenRentList' },
  { id: 'rent-detail',    label: '05 · Alquiler · detalle', comp: 'ScreenRentDetail' },
  { id: 'carpool-search', label: '06 · Buscar carpool',   comp: 'ScreenCarpoolSearch' },
  { id: 'carpool-publish',label: '07 · Publicar ruta',    comp: 'ScreenCarpoolPublish' },
  { id: 'trip-active',    label: '08 · Viaje en curso',   comp: 'ScreenTripActive' },
  { id: 'payment',        label: '09 · Pago seguro',      comp: 'ScreenPayment' },
  { id: 'owner',          label: '10 · Mi flota (propietario)', comp: 'ScreenOwner' },
  { id: 'safety',         label: '11 · Seguridad',        comp: 'ScreenSafety' },
  { id: 'chat',           label: '12 · Chat con conductor', comp: 'ScreenChat' },
  { id: 'rewards',        label: '13 · Recompensas',      comp: 'ScreenRewards' },
  { id: 'profile',        label: '14 · Perfil + reseñas', comp: 'ScreenProfile' },
];

const FONT_OPTIONS = {
  Manrope: 'Manrope, system-ui, sans-serif',
  Geist: '"Geist", "Inter", system-ui, sans-serif',
  Jakarta: '"Plus Jakarta Sans", system-ui, sans-serif',
  Editorial: '"Fraunces", "Georgia", serif',
};

const App = () => {
  const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
    "mode": "dark",
    "accent": "blue",
    "role": "pasajero",
    "lang": "es",
    "density": "comoda",
    "font": "Manrope",
    "showAll": true,
    "primaryScreen": "home"
  }/*EDITMODE-END*/;

  const [tw, setTweak] = useTweaks(TWEAK_DEFAULTS);

  const theme = useMemo(() => makeTheme(tw.mode, tw.accent), [tw.mode, tw.accent]);
  const t = I18N[tw.lang] || I18N.es;
  const accent = theme.accent;

  // Per-artboard nav state — let any artboard internally navigate without affecting siblings
  // We give each artboard its own React state via a small wrapper
  const Artboard = ({ initial }) => {
    const [screenId, setScreenId] = useState(initial);
    const meta = ALL_SCREENS.find(s => s.id === screenId) || ALL_SCREENS[0];
    const Comp = window[meta.comp];
    if (!Comp) return <div style={{ padding: 20, color: theme.text }}>Falta {meta.comp}</div>;
    return (
      <Phone theme={theme} density={tw.density}>
        <Comp theme={theme} t={t} accent={accent} role={tw.role} density={tw.density} onNav={(id) => setScreenId(id)}/>
      </Phone>
    );
  };

  return (
    <div style={{ fontFamily: FONT_OPTIONS[tw.font] || FONT_OPTIONS.Manrope, color: theme.text }}>
      <style>{`
        body { background: ${tw.mode === 'dark' ? '#0A0E14' : '#F0EEE9'}; }
        * { -webkit-font-smoothing: antialiased; }
      `}</style>

      <DesignCanvas
        title="WheelsPe Mobile"
        subtitle="App de movilidad colaborativa peruana — alquiler entre particulares + carpooling. 14 pantallas, navegables internamente. Usa Tweaks para cambiar tema, rol, idioma y más."
      >
        <DCSection id="hero" title="Flujo principal" subtitle="Entrada → KYC → Home → Reserva">
          {ALL_SCREENS.slice(0, 3).map(s => (
            <DCArtboard key={s.id} id={s.id} label={s.label} width={360} height={740}>
              <Artboard initial={s.id}/>
            </DCArtboard>
          ))}
        </DCSection>

        <DCSection id="rental" title="Alquiler de vehículo" subtitle="Catálogo, detalle y pago">
          {['rent-list','rent-detail','payment'].map(id => {
            const s = ALL_SCREENS.find(x => x.id === id);
            return (
              <DCArtboard key={s.id} id={s.id} label={s.label} width={360} height={740}>
                <Artboard initial={s.id}/>
              </DCArtboard>
            );
          })}
        </DCSection>

        <DCSection id="carpool" title="Carpooling" subtitle="Buscar, publicar, viaje en curso, chat">
          {['carpool-search','carpool-publish','trip-active','chat'].map(id => {
            const s = ALL_SCREENS.find(x => x.id === id);
            return (
              <DCArtboard key={s.id} id={s.id} label={s.label} width={360} height={740}>
                <Artboard initial={s.id}/>
              </DCArtboard>
            );
          })}
        </DCSection>

        <DCSection id="owner" title="Vista del propietario" subtitle="Quien publica su auto">
          {['owner'].map(id => {
            const s = ALL_SCREENS.find(x => x.id === id);
            return (
              <DCArtboard key={s.id} id={s.id} label={s.label} width={360} height={740}>
                <Artboard initial={s.id}/>
              </DCArtboard>
            );
          })}
        </DCSection>

        <DCSection id="trust" title="Confianza y comunidad" subtitle="Seguridad, reseñas, fidelización">
          {['safety','profile','rewards'].map(id => {
            const s = ALL_SCREENS.find(x => x.id === id);
            return (
              <DCArtboard key={s.id} id={s.id} label={s.label} width={360} height={740}>
                <Artboard initial={s.id}/>
              </DCArtboard>
            );
          })}
        </DCSection>

        <DCPostIt x={20} y={40} color="yellow">
          <strong>WheelsPe</strong><br/>
          Toca un artboard para entrar en modo enfoque y navegar como una app real.<br/><br/>
          Usa <strong>Tweaks</strong> (icono arriba) para cambiar tema, color de acento, rol del usuario, idioma (ES/QU/EN), tipografía y densidad.
        </DCPostIt>
      </DesignCanvas>

      {/* Tweaks panel */}
      <TweaksPanel title="Tweaks">
        <TweakSection label="Apariencia">
          <TweakRadio label="Tema" value={tw.mode} onChange={v => setTweak('mode', v)} options={[
            { value: 'dark', label: 'Oscuro' },
            { value: 'light', label: 'Claro' },
          ]}/>
          <TweakSelect label="Acento" value={tw.accent} onChange={v => setTweak('accent', v)} options={[
            { value: 'blue', label: 'Azul cobalto' },
            { value: 'azul', label: 'Cielo andino' },
            { value: 'terra', label: 'Terracota' },
            { value: 'oro', label: 'Oro inca' },
          ]}/>
          <TweakSelect label="Tipografía" value={tw.font} onChange={v => setTweak('font', v)} options={[
            { value: 'Manrope', label: 'Manrope (recomendada)' },
            { value: 'Geist', label: 'Geist / Inter' },
            { value: 'Jakarta', label: 'Plus Jakarta Sans' },
            { value: 'Editorial', label: 'Fraunces (editorial)' },
          ]}/>
          <TweakRadio label="Densidad" value={tw.density} onChange={v => setTweak('density', v)} options={[
            { value: 'compacta', label: 'Compacta' },
            { value: 'comoda', label: 'Cómoda' },
            { value: 'espaciosa', label: 'Espaciosa' },
          ]}/>
        </TweakSection>

        <TweakSection label="Contexto del usuario">
          <TweakRadio label="Rol activo" value={tw.role} onChange={v => setTweak('role', v)} options={[
            { value: 'pasajero', label: 'Pasajero' },
            { value: 'conductor', label: 'Conductor' },
            { value: 'propietario', label: 'Propietario' },
          ]}/>
          <TweakRadio label="Idioma" value={tw.lang} onChange={v => setTweak('lang', v)} options={[
            { value: 'es', label: 'Español' },
            { value: 'qu', label: 'Quechua' },
            { value: 'en', label: 'English' },
          ]}/>
        </TweakSection>
      </TweaksPanel>
    </div>
  );
};

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App/>);
