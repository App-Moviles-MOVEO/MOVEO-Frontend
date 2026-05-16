// tokens.jsx — Design tokens + theme + i18n + role context
// Minimal premium theme inspired by Revolut/Lyft new

const ACCENT_PALETTES = {
  blue:  { 500: '#3B82F6', 600: '#2563EB', 50: '#EFF6FF', glow: 'rgba(59,130,246,.35)' },
  azul:  { 500: '#0EA5E9', 600: '#0284C7', 50: '#F0F9FF', glow: 'rgba(14,165,233,.35)' },
  terra: { 500: '#C2410C', 600: '#9A3412', 50: '#FFF7ED', glow: 'rgba(194,65,12,.30)' },
  oro:   { 500: '#D4A24C', 600: '#A47831', 50: '#FBF7EE', glow: 'rgba(212,162,76,.30)' },
};

function makeTheme(mode, accentKey) {
  const A = ACCENT_PALETTES[accentKey] || ACCENT_PALETTES.blue;
  if (mode === 'dark') {
    return {
      mode: 'dark',
      bg: '#0A0E14',
      surface: '#10161F',
      surfaceAlt: '#161D28',
      surfaceHi: '#1C2532',
      border: 'rgba(255,255,255,0.08)',
      borderStrong: 'rgba(255,255,255,0.14)',
      text: '#F4F6F9',
      textMuted: 'rgba(244,246,249,0.62)',
      textFaint: 'rgba(244,246,249,0.42)',
      accent: A[500],
      accentHover: A[600],
      accentSoft: 'rgba(59,130,246,0.14)',
      accentGlow: A.glow,
      onAccent: '#FFFFFF',
      success: '#34D399',
      warn: '#F59E0B',
      danger: '#F87171',
      shadow: '0 24px 60px rgba(0,0,0,.45)',
    };
  }
  return {
    mode: 'light',
    bg: '#F6F7F9',
    surface: '#FFFFFF',
    surfaceAlt: '#F1F3F6',
    surfaceHi: '#E8ECF1',
    border: 'rgba(10,14,20,0.08)',
    borderStrong: 'rgba(10,14,20,0.14)',
    text: '#0A0E14',
    textMuted: 'rgba(10,14,20,0.62)',
    textFaint: 'rgba(10,14,20,0.42)',
    accent: A[500],
    accentHover: A[600],
    accentSoft: 'rgba(59,130,246,0.10)',
    accentGlow: A.glow,
    onAccent: '#FFFFFF',
    success: '#059669',
    warn: '#D97706',
    danger: '#DC2626',
    shadow: '0 18px 40px rgba(10,14,20,.10)',
  };
}

// Densidad
const DENSITY = {
  comoda: { pad: 16, gap: 12, radius: 16, item: 56 },
  compacta: { pad: 12, gap: 8, radius: 12, item: 48 },
  espaciosa: { pad: 20, gap: 16, radius: 20, item: 64 },
};

// i18n minimal — cubre títulos clave por pantalla
const I18N = {
  es: {
    appName: 'WheelsPe',
    greeting: 'Hola',
    where: '¿A dónde vamos?',
    rent: 'Alquilar', share: 'Compartir', drive: 'Conducir', myAuto: 'Mi auto',
    home: 'Inicio', trips: 'Viajes', wallet: 'Billetera', profile: 'Perfil',
    rentCar: 'Alquilar auto', shareRoute: 'Compartir ruta', publishRoute: 'Publicar ruta',
    findRoute: 'Buscar ruta', startVerification: 'Verificar identidad',
    pay: 'Pagar', payNow: 'Pagar ahora', continue: 'Continuar', book: 'Reservar',
    confirm: 'Confirmar', cancel: 'Cancelar', startTrip: 'Iniciar viaje',
    panic: 'SOS', emergency: 'Emergencia', shareTrip: 'Compartir viaje',
    onlyWomen: 'Solo Mujeres', trustedContacts: 'Contactos de confianza',
    rating: 'Reputación', reviews: 'Reseñas', incentives: 'Recompensas',
    messages: 'Mensajes', perDay: '/día', perSeat: '/asiento', seats: 'asientos',
    available: 'Disponible', verified: 'Verificado', distance: 'distancia',
    welcome: 'Bienvenido a', tagline: 'Tu auto, tu ruta, tu Perú.',
    signIn: 'Ingresar', signUp: 'Crear cuenta', orContinue: 'o continúa con',
    scanDni: 'Escanea tu DNI', dniHint: 'Coloca el frente de tu documento dentro del marco',
    chooseRole: '¿Qué necesitas hoy?', renter: 'Alquilar un auto',
    carpool: 'Compartir un viaje', owner: 'Publicar mi auto',
    routes: 'Rutas', myCars: 'Mis autos', earnings: 'Ingresos',
  },
  qu: {
    appName: 'WheelsPe',
    greeting: 'Allillanchu',
    where: '¿Maytan rinki?',
    rent: 'Mañariy', share: 'Rakinakuy', drive: 'Apay', myAuto: 'Ñoqap autoy',
    home: 'Qallariy', trips: 'Puriykuna', wallet: 'Qollqe', profile: 'Sutiy',
    rentCar: 'Auto mañariy', shareRoute: 'Ñan rakinakuy', publishRoute: 'Ñan willay',
    findRoute: 'Ñan maskay', startVerification: 'Riqsichikuy',
    pay: 'Pagay', payNow: 'Kunan pagay', continue: 'Kachaykuy', book: 'Munay',
    confirm: 'Arí niy', cancel: 'Saqiy', startTrip: 'Puriyta qallariy',
    panic: 'YANAPAY', emergency: 'Yanapay', shareTrip: 'Puriyta rakiy',
    onlyWomen: 'Warmillaña', trustedContacts: 'Iñiy masikuna',
    rating: 'Sumaq kay', reviews: 'Rimaykuna', incentives: 'Saminchaykuna',
    messages: 'Willakuykuna', perDay: '/p\'unchay', perSeat: '/tiyana', seats: 'tiyanakuna',
    available: 'Kachkan', verified: 'Riqsichisqa', distance: 'karu',
    welcome: 'Hamuy kay',  tagline: 'Autoyki, ñanniyki, Perú llaqtayki.',
    signIn: 'Yaykuy', signUp: 'Ruway', orContinue: 'utaq kayqa',
    scanDni: 'DNI-yki escanéay', dniHint: 'Ñawpaqenta marku ukhuman churay',
    chooseRole: '¿Imatan munanki kunan?', renter: 'Auto mañariy',
    carpool: 'Puriy rakinakuy', owner: 'Autoyta willay',
    routes: 'Ñankuna', myCars: 'Autoykuna', earnings: 'Qollqe huñukuy',
  },
  en: {
    appName: 'WheelsPe',
    greeting: 'Hi',
    where: 'Where to?',
    rent: 'Rent', share: 'Share', drive: 'Drive', myAuto: 'My car',
    home: 'Home', trips: 'Trips', wallet: 'Wallet', profile: 'Profile',
    rentCar: 'Rent a car', shareRoute: 'Share a ride', publishRoute: 'Publish route',
    findRoute: 'Find a ride', startVerification: 'Verify identity',
    pay: 'Pay', payNow: 'Pay now', continue: 'Continue', book: 'Book',
    confirm: 'Confirm', cancel: 'Cancel', startTrip: 'Start trip',
    panic: 'SOS', emergency: 'Emergency', shareTrip: 'Share trip',
    onlyWomen: 'Women only', trustedContacts: 'Trusted contacts',
    rating: 'Rating', reviews: 'Reviews', incentives: 'Rewards',
    messages: 'Messages', perDay: '/day', perSeat: '/seat', seats: 'seats',
    available: 'Available', verified: 'Verified', distance: 'away',
    welcome: 'Welcome to', tagline: 'Your car, your route, your Peru.',
    signIn: 'Sign in', signUp: 'Create account', orContinue: 'or continue with',
    scanDni: 'Scan your ID', dniHint: 'Place the front of your ID inside the frame',
    chooseRole: 'What do you need today?', renter: 'Rent a car',
    carpool: 'Share a ride', owner: 'List my car',
    routes: 'Routes', myCars: 'My cars', earnings: 'Earnings',
  },
};

window.ACCENT_PALETTES = ACCENT_PALETTES;
window.makeTheme = makeTheme;
window.DENSITY = DENSITY;
window.I18N = I18N;
