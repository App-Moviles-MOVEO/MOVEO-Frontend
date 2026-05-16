// ui.jsx — Base primitives shared across all WheelsPe screens
// Icons (line-style, 24px stroke=1.6) + buttons + chips + inputs + avatars

const Icon = ({ name, size = 22, stroke = 1.6, color = 'currentColor', style }) => {
  const s = { width: size, height: size, color, ...(style || {}) };
  const props = {
    viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor',
    strokeWidth: stroke, strokeLinecap: 'round', strokeLinejoin: 'round',
    style: s,
  };
  switch (name) {
    case 'arrow-left': return <svg {...props}><path d="M15 6l-6 6 6 6"/></svg>;
    case 'arrow-right': return <svg {...props}><path d="M9 6l6 6-6 6"/></svg>;
    case 'chevron-right': return <svg {...props}><path d="M9 6l6 6-6 6"/></svg>;
    case 'chevron-down': return <svg {...props}><path d="M6 9l6 6 6-6"/></svg>;
    case 'close': return <svg {...props}><path d="M6 6l12 12M18 6L6 18"/></svg>;
    case 'menu': return <svg {...props}><path d="M4 7h16M4 12h16M4 17h16"/></svg>;
    case 'search': return <svg {...props}><circle cx="11" cy="11" r="7"/><path d="M20 20l-3.5-3.5"/></svg>;
    case 'filter': return <svg {...props}><path d="M4 6h16M7 12h10M10 18h4"/></svg>;
    case 'home': return <svg {...props}><path d="M4 11l8-7 8 7v9a1 1 0 01-1 1h-4v-6h-6v6H5a1 1 0 01-1-1z"/></svg>;
    case 'car': return <svg {...props}><path d="M5 17h14M6 17v2M18 17v2M5 13l1.5-5a2 2 0 012-1.5h7a2 2 0 012 1.5L19 13M4 13h16v3a1 1 0 01-1 1H5a1 1 0 01-1-1z"/><circle cx="8" cy="15" r="1"/><circle cx="16" cy="15" r="1"/></svg>;
    case 'route': return <svg {...props}><circle cx="6" cy="6" r="2"/><circle cx="18" cy="18" r="2"/><path d="M8 6h6a4 4 0 014 4v0a4 4 0 01-4 4h-4a4 4 0 00-4 4"/></svg>;
    case 'wallet': return <svg {...props}><rect x="3" y="6" width="18" height="13" rx="2"/><path d="M16 12.5h2.5"/><path d="M3 9h14a4 4 0 014 4"/></svg>;
    case 'user': return <svg {...props}><circle cx="12" cy="8" r="4"/><path d="M4 20a8 8 0 0116 0"/></svg>;
    case 'pin': return <svg {...props}><path d="M12 22s7-7.5 7-13a7 7 0 10-14 0c0 5.5 7 13 7 13z"/><circle cx="12" cy="9" r="2.5"/></svg>;
    case 'pin-fill': return <svg viewBox="0 0 24 24" style={s} fill="currentColor"><path d="M12 22s7-7.5 7-13a7 7 0 10-14 0c0 5.5 7 13 7 13zm0-11a2.5 2.5 0 110-5 2.5 2.5 0 010 5z"/></svg>;
    case 'star': return <svg {...props} fill="currentColor" stroke="none"><path d="M12 3l2.6 5.6 6 .8-4.4 4.2 1.1 6L12 16.8 6.7 19.6l1.1-6L3.4 9.4l6-.8L12 3z"/></svg>;
    case 'star-line': return <svg {...props}><path d="M12 3l2.6 5.6 6 .8-4.4 4.2 1.1 6L12 16.8 6.7 19.6l1.1-6L3.4 9.4l6-.8L12 3z"/></svg>;
    case 'shield': return <svg {...props}><path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z"/><path d="M9 12l2 2 4-4"/></svg>;
    case 'sos': return <svg {...props}><circle cx="12" cy="12" r="9"/><path d="M9 9.5a1.5 1.5 0 113 0c0 .8-1.5 1-1.5 2"/><circle cx="14.5" cy="14" r="1.4"/></svg>;
    case 'phone': return <svg {...props}><path d="M5 4h3l2 5-2 1a11 11 0 006 6l1-2 5 2v3a2 2 0 01-2 2A16 16 0 013 6a2 2 0 012-2z"/></svg>;
    case 'message': return <svg {...props}><path d="M4 6a2 2 0 012-2h12a2 2 0 012 2v8a2 2 0 01-2 2H9l-4 4v-4H6a2 2 0 01-2-2z"/></svg>;
    case 'check': return <svg {...props}><path d="M5 12l4 4 10-10"/></svg>;
    case 'check-circle': return <svg viewBox="0 0 24 24" style={s} fill="currentColor"><path d="M12 2a10 10 0 100 20 10 10 0 000-20zm-1 14.5l-4-4 1.4-1.4 2.6 2.6 5.6-5.6L18 9.5z"/></svg>;
    case 'plus': return <svg {...props}><path d="M12 5v14M5 12h14"/></svg>;
    case 'calendar': return <svg {...props}><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/></svg>;
    case 'clock': return <svg {...props}><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>;
    case 'users': return <svg {...props}><circle cx="9" cy="8" r="3.5"/><path d="M3 19a6 6 0 0112 0"/><circle cx="17" cy="9" r="2.8"/><path d="M16 14a5 5 0 015 5"/></svg>;
    case 'female': return <svg {...props}><circle cx="12" cy="9" r="5"/><path d="M12 14v7M9 18h6"/></svg>;
    case 'sun': return <svg {...props}><circle cx="12" cy="12" r="4"/><path d="M12 3v2M12 19v2M3 12h2M19 12h2M5.6 5.6l1.4 1.4M17 17l1.4 1.4M5.6 18.4L7 17M17 7l1.4-1.4"/></svg>;
    case 'moon': return <svg {...props}><path d="M20 14a8 8 0 11-10-10 7 7 0 0010 10z"/></svg>;
    case 'card': return <svg {...props}><rect x="3" y="6" width="18" height="13" rx="2"/><path d="M3 10h18"/></svg>;
    case 'lock': return <svg {...props}><rect x="5" y="11" width="14" height="9" rx="2"/><path d="M8 11V8a4 4 0 018 0v3"/></svg>;
    case 'qr': return <svg {...props}><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><path d="M14 14h3v3M21 17v4M14 21h3"/></svg>;
    case 'gift': return <svg {...props}><rect x="3" y="9" width="18" height="11" rx="2"/><path d="M3 13h18M12 9v11M8 9c-2 0-3-3-1-4s4 4 4 4-4 0-3-4z" /></svg>;
    case 'bell': return <svg {...props}><path d="M6 16V11a6 6 0 1112 0v5l1.5 2H4.5z"/><path d="M10 21a2 2 0 004 0"/></svg>;
    case 'settings': return <svg {...props}><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 11-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 11-4 0v-.09a1.65 1.65 0 00-1.08-1.51 1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 11-2.83-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 110-4h.09a1.65 1.65 0 001.51-1.08 1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 112.83-2.83l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 114 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 112.83 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 110 4h-.09a1.65 1.65 0 00-1.51 1z"/></svg>;
    case 'trending': return <svg {...props}><path d="M3 17l6-6 4 4 8-8"/><path d="M14 7h7v7"/></svg>;
    case 'fuel': return <svg {...props}><rect x="4" y="4" width="10" height="16" rx="1"/><path d="M14 9h2a2 2 0 012 2v5a1.5 1.5 0 003 0V8l-3-3"/></svg>;
    case 'seat': return <svg {...props}><path d="M5 21v-7a3 3 0 013-3h2v10M14 11V6a3 3 0 013-3h0a3 3 0 013 3v8H10"/></svg>;
    case 'sparkle': return <svg {...props}><path d="M12 3l2 5 5 2-5 2-2 5-2-5-5-2 5-2z"/></svg>;
    case 'leaf': return <svg {...props}><path d="M5 19c0-8 6-14 14-14 0 8-6 14-14 14z"/><path d="M5 19c4-4 8-6 12-7"/></svg>;
    default: return <svg {...props}><circle cx="12" cy="12" r="8"/></svg>;
  }
};

// Avatar — gradient initial badge (generic placeholder, no real photos)
const Avatar = ({ name = '', size = 40, hue = 220, ring = false, ringColor }) => {
  const initial = (name || 'U').trim().charAt(0).toUpperCase();
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%', flexShrink: 0,
      background: `linear-gradient(135deg, oklch(0.62 0.13 ${hue}), oklch(0.42 0.16 ${(hue + 40) % 360}))`,
      color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontWeight: 700, fontSize: size * 0.42, letterSpacing: '-0.02em',
      boxShadow: ring ? `0 0 0 2px ${ringColor || '#fff'}, 0 0 0 4px currentColor` : 'none',
    }}>{initial}</div>
  );
};

// CarPlaceholder — striped SVG with monospace label, looks deliberately "placeholder"
const CarPlaceholder = ({ label = 'foto del auto', height = 140, theme, accent }) => {
  const dark = theme?.mode === 'dark';
  const stripeA = dark ? 'rgba(255,255,255,.04)' : 'rgba(10,14,20,.04)';
  const stripeB = dark ? 'rgba(255,255,255,.07)' : 'rgba(10,14,20,.07)';
  return (
    <div style={{
      width: '100%', height, borderRadius: 14, overflow: 'hidden', position: 'relative',
      background: `repeating-linear-gradient(45deg, ${stripeA} 0 8px, ${stripeB} 8px 16px)`,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      border: `1px solid ${theme?.border || 'rgba(0,0,0,.06)'}`,
    }}>
      <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Icon name="car" size={Math.min(height * 0.45, 72)} color={dark ? 'rgba(255,255,255,.18)' : 'rgba(10,14,20,.18)'} stroke={1.2} />
      </div>
      <div style={{
        position: 'absolute', bottom: 8, left: 10, fontFamily: 'JetBrains Mono, ui-monospace, monospace',
        fontSize: 10, color: theme?.textMuted || 'rgba(0,0,0,.5)', letterSpacing: '.04em',
      }}>{label}</div>
    </div>
  );
};

// Map placeholder — abstract grid/route lines, no fake imagery
const MapPlaceholder = ({ theme, accent, showRoute = true, showPin = true, height = '100%' }) => {
  const dark = theme?.mode === 'dark';
  const grid = dark ? 'rgba(255,255,255,.04)' : 'rgba(10,14,20,.05)';
  const land = dark ? '#0E141C' : '#EEF1F4';
  const water = dark ? '#0B1019' : '#E2E8EE';
  return (
    <div style={{
      position: 'relative', width: '100%', height,
      background: land, overflow: 'hidden',
    }}>
      {/* horizontal water band */}
      <div style={{ position: 'absolute', left: 0, right: 0, top: '60%', height: '40%', background: water }} />
      {/* grid */}
      <svg width="100%" height="100%" style={{ position: 'absolute', inset: 0 }}>
        <defs>
          <pattern id="g" width="32" height="32" patternUnits="userSpaceOnUse">
            <path d="M0 0H32M0 0V32" stroke={grid} strokeWidth="1"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#g)"/>
        {/* road network */}
        <path d="M-10 80 Q 100 60 200 130 T 450 200" stroke={dark ? 'rgba(255,255,255,.08)' : 'rgba(10,14,20,.10)'} strokeWidth="14" fill="none" strokeLinecap="round"/>
        <path d="M40 -10 Q 80 100 150 200 T 280 460" stroke={dark ? 'rgba(255,255,255,.06)' : 'rgba(10,14,20,.07)'} strokeWidth="10" fill="none" strokeLinecap="round"/>
        <path d="M380 -10 Q 360 200 240 320" stroke={dark ? 'rgba(255,255,255,.06)' : 'rgba(10,14,20,.07)'} strokeWidth="10" fill="none" strokeLinecap="round"/>
        {showRoute && (
          <path d="M50 350 Q 140 280 200 240 T 340 130" stroke={accent} strokeWidth="4" fill="none"
                strokeLinecap="round" strokeDasharray="0" />
        )}
        {showRoute && (
          <circle cx="50" cy="350" r="6" fill={accent}/>
        )}
      </svg>
      {showPin && (
        <div style={{ position: 'absolute', left: '70%', top: '32%', transform: 'translate(-50%, -100%)', color: accent, filter: `drop-shadow(0 4px 8px ${accent}55)` }}>
          <Icon name="pin-fill" size={36}/>
        </div>
      )}
    </div>
  );
};

// Button
const Btn = ({ children, kind = 'primary', size = 'lg', icon, theme, full, onClick, style }) => {
  const sizes = { lg: { h: 52, px: 22, fs: 16, gap: 10 }, md: { h: 44, px: 18, fs: 14, gap: 8 }, sm: { h: 34, px: 14, fs: 13, gap: 6 } };
  const sz = sizes[size];
  const styles = {
    primary: { bg: theme.text, color: theme.bg, border: 'none' },
    accent: { bg: theme.accent, color: theme.onAccent, border: 'none' },
    ghost: { bg: 'transparent', color: theme.text, border: `1px solid ${theme.borderStrong}` },
    soft: { bg: theme.surfaceAlt, color: theme.text, border: 'none' },
    danger: { bg: theme.danger, color: '#fff', border: 'none' },
  };
  const s = styles[kind];
  return (
    <button onClick={onClick} style={{
      height: sz.h, padding: `0 ${sz.px}px`, borderRadius: 999,
      background: s.bg, color: s.color, border: s.border,
      fontFamily: 'inherit', fontSize: sz.fs, fontWeight: 600, letterSpacing: '-0.01em',
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: sz.gap,
      width: full ? '100%' : 'auto', cursor: 'pointer', ...style,
    }}>
      {icon && <Icon name={icon} size={sz.fs + 4}/>}
      {children}
    </button>
  );
};

// Chip
const Chip = ({ children, active, theme, icon, onClick }) => (
  <button onClick={onClick} style={{
    height: 36, padding: '0 14px', borderRadius: 999, border: `1px solid ${active ? theme.text : theme.border}`,
    background: active ? theme.text : 'transparent', color: active ? theme.bg : theme.textMuted,
    fontFamily: 'inherit', fontSize: 13, fontWeight: 600, display: 'inline-flex',
    alignItems: 'center', gap: 6, cursor: 'pointer', whiteSpace: 'nowrap',
  }}>
    {icon && <Icon name={icon} size={14}/>}
    {children}
  </button>
);

// StatRow
const Stat = ({ label, value, theme, accent }) => (
  <div style={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
    <span style={{ fontSize: 11, fontWeight: 600, letterSpacing: '.06em', textTransform: 'uppercase', color: theme.textFaint }}>{label}</span>
    <span style={{ fontSize: 18, fontWeight: 700, color: accent || theme.text, letterSpacing: '-0.02em' }}>{value}</span>
  </div>
);

// Card — surface with rounded corners
const Card = ({ children, theme, style, padded = true }) => (
  <div style={{
    background: theme.surface,
    border: `1px solid ${theme.border}`,
    borderRadius: 18,
    padding: padded ? 16 : 0,
    ...style,
  }}>{children}</div>
);

// Bottom Nav
const BottomNav = ({ active, onTab, theme, t }) => {
  const tabs = [
    { id: 'home', icon: 'home', label: t.home },
    { id: 'trips', icon: 'route', label: t.trips },
    { id: 'wallet', icon: 'wallet', label: t.wallet },
    { id: 'profile', icon: 'user', label: t.profile },
  ];
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-around', padding: '10px 8px 6px',
      background: theme.surface, borderTop: `1px solid ${theme.border}`,
    }}>
      {tabs.map(tab => {
        const isActive = active === tab.id;
        return (
          <button key={tab.id} onClick={() => onTab && onTab(tab.id)} style={{
            background: 'none', border: 'none', cursor: 'pointer',
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3, padding: '4px 14px',
            color: isActive ? theme.text : theme.textFaint, fontFamily: 'inherit',
          }}>
            <Icon name={tab.icon} size={22} stroke={isActive ? 2 : 1.6}/>
            <span style={{ fontSize: 10.5, fontWeight: isActive ? 700 : 500, letterSpacing: '-0.01em' }}>{tab.label}</span>
          </button>
        );
      })}
    </div>
  );
};

// Section header
const SectionHead = ({ title, action, onAction, theme }) => (
  <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', padding: '0 0 12px' }}>
    <h3 style={{ margin: 0, fontSize: 16, fontWeight: 700, color: theme.text, letterSpacing: '-0.02em' }}>{title}</h3>
    {action && (
      <button onClick={onAction} style={{
        background: 'none', border: 'none', color: theme.accent, fontFamily: 'inherit',
        fontSize: 13, fontWeight: 600, cursor: 'pointer', padding: 0,
      }}>{action}</button>
    )}
  </div>
);

Object.assign(window, {
  Icon, Avatar, CarPlaceholder, MapPlaceholder, Btn, Chip, Stat, Card, BottomNav, SectionHead,
});
