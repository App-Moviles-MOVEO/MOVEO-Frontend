// screens-auth.jsx — Onboarding, Login, KYC

const ScreenOnboarding = ({ theme, t, accent, onNav }) => (
  <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text, padding: 24 }}>
    {/* Logo mark */}
    <div style={{ paddingTop: 40, display: 'flex', alignItems: 'center', gap: 10 }}>
      <div style={{ width: 36, height: 36, borderRadius: 10, background: theme.text, display: 'flex', alignItems: 'center', justifyContent: 'center', color: theme.bg, fontWeight: 800, fontSize: 18, letterSpacing: '-0.05em' }}>W</div>
      <span style={{ fontSize: 18, fontWeight: 700, letterSpacing: '-0.03em' }}>WheelsPe</span>
    </div>

    {/* Hero abstract — concentric arcs */}
    <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative', minHeight: 240 }}>
      <svg width="280" height="280" viewBox="0 0 280 280">
        <defs>
          <linearGradient id="grad1" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor={accent} stopOpacity="0.9"/>
            <stop offset="1" stopColor={accent} stopOpacity="0.0"/>
          </linearGradient>
        </defs>
        <circle cx="140" cy="140" r="130" fill="none" stroke={theme.border} strokeWidth="1"/>
        <circle cx="140" cy="140" r="100" fill="none" stroke={theme.border} strokeWidth="1"/>
        <circle cx="140" cy="140" r="70" fill="none" stroke={theme.border} strokeWidth="1"/>
        <path d="M 140 10 A 130 130 0 0 1 270 140" stroke="url(#grad1)" strokeWidth="3" fill="none" strokeLinecap="round"/>
        <path d="M 40 140 A 100 100 0 0 1 140 40" stroke={accent} strokeWidth="3" fill="none" strokeLinecap="round" opacity="0.6"/>
        <circle cx="140" cy="10" r="6" fill={accent}/>
        <circle cx="40" cy="140" r="5" fill={theme.text}/>
      </svg>
    </div>

    <div style={{ display: 'flex', flexDirection: 'column', gap: 8, paddingBottom: 16 }}>
      <span style={{ fontSize: 14, color: theme.textMuted, fontWeight: 500 }}>{t.welcome}</span>
      <h1 style={{ margin: 0, fontSize: 42, fontWeight: 800, letterSpacing: '-0.04em', lineHeight: 1.0 }}>WheelsPe.</h1>
      <p style={{ margin: '8px 0 0', fontSize: 15, color: theme.textMuted, lineHeight: 1.5, textWrap: 'pretty' }}>{t.tagline}</p>
    </div>

    <div style={{ display: 'flex', flexDirection: 'column', gap: 10, paddingBottom: 8 }}>
      <Btn kind="accent" full theme={theme} onClick={() => onNav('kyc')}>{t.signUp}</Btn>
      <Btn kind="ghost" full theme={theme} onClick={() => onNav('home')}>{t.signIn}</Btn>
    </div>
  </div>
);

const ScreenKyc = ({ theme, t, accent, onNav }) => {
  const [step, setStep] = React.useState(0);
  const steps = ['DNI frente', 'DNI reverso', 'Selfie', 'Listo'];
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: theme.bg, color: theme.text }}>
      <div style={{ padding: '16px 20px 8px', display: 'flex', alignItems: 'center', gap: 14 }}>
        <button onClick={() => onNav('onboarding')} style={{ background: theme.surfaceAlt, border: 'none', width: 40, height: 40, borderRadius: 12, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', color: theme.text }}>
          <Icon name="arrow-left" size={20}/>
        </button>
        <div style={{ flex: 1, display: 'flex', gap: 4 }}>
          {steps.map((_, i) => (
            <div key={i} style={{ flex: 1, height: 3, borderRadius: 2, background: i <= step ? accent : theme.surfaceHi }}/>
          ))}
        </div>
      </div>

      <div style={{ padding: '16px 24px 0' }}>
        <span style={{ fontSize: 12, fontWeight: 700, letterSpacing: '.08em', textTransform: 'uppercase', color: theme.textFaint }}>Paso {step + 1} de 4</span>
        <h1 style={{ margin: '8px 0 6px', fontSize: 28, fontWeight: 800, letterSpacing: '-0.03em' }}>{t.scanDni}</h1>
        <p style={{ margin: 0, fontSize: 14, color: theme.textMuted, lineHeight: 1.5 }}>{t.dniHint}</p>
      </div>

      {/* Camera frame */}
      <div style={{ padding: 24, flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div style={{ width: '100%', aspectRatio: '0.63', borderRadius: 18, background: theme.surfaceAlt, border: `2px dashed ${theme.borderStrong}`, position: 'relative', overflow: 'hidden' }}>
          {/* corner marks */}
          {[[8,8,'tl'],[8,8,'tr'],[8,8,'bl'],[8,8,'br']].map(([x,y,k], i) => {
            const pos = { tl:{top:14,left:14,br:'4px 0 0 0'}, tr:{top:14,right:14,br:'0 4px 0 0'}, bl:{bottom:14,left:14,br:'0 0 0 4px'}, br:{bottom:14,right:14,br:'0 0 4px 0'} }[k];
            const corners = ['tl','tr','bl','br'][i];
            const p = { tl:{top:14,left:14}, tr:{top:14,right:14}, bl:{bottom:14,left:14}, br:{bottom:14,right:14} }[corners];
            return <div key={i} style={{ position: 'absolute', ...p, width: 28, height: 28, borderColor: accent, borderStyle: 'solid', borderWidth: 0, ...({ tl:{borderTopWidth:3,borderLeftWidth:3,borderTopLeftRadius:8}, tr:{borderTopWidth:3,borderRightWidth:3,borderTopRightRadius:8}, bl:{borderBottomWidth:3,borderLeftWidth:3,borderBottomLeftRadius:8}, br:{borderBottomWidth:3,borderRightWidth:3,borderBottomRightRadius:8} }[corners]) }}/>;
          })}
          {/* DNI placeholder content */}
          <div style={{ position: 'absolute', inset: 24, opacity: 0.3, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: theme.textMuted, letterSpacing: '.1em' }}>REPÚBLICA DEL PERÚ</div>
            <div style={{ display: 'flex', gap: 10 }}>
              <div style={{ width: 64, height: 80, borderRadius: 4, background: theme.surfaceHi }}/>
              <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 6, justifyContent: 'center' }}>
                {[60, 80, 50].map((w,i) => <div key={i} style={{ height: 4, width: `${w}%`, borderRadius: 2, background: theme.surfaceHi }}/>)}
              </div>
            </div>
            <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 8, color: theme.textMuted }}>0&lt;&lt;&lt;DNI&lt;PER&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;</div>
          </div>
        </div>
      </div>

      <div style={{ padding: 20, display: 'flex', flexDirection: 'column', gap: 10 }}>
        <Btn kind="accent" full theme={theme} icon="qr" onClick={() => step < 3 ? setStep(step + 1) : onNav('home')}>
          {step === 3 ? '¡Listo!' : 'Capturar'}
        </Btn>
        <button style={{ background: 'none', border: 'none', color: theme.textMuted, fontFamily: 'inherit', fontSize: 13, fontWeight: 500, cursor: 'pointer', padding: 8 }}>
          ¿Problemas? Subir foto manualmente
        </button>
      </div>
    </div>
  );
};

window.ScreenOnboarding = ScreenOnboarding;
window.ScreenKyc = ScreenKyc;
