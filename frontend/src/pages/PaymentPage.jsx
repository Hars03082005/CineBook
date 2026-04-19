import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';

export default function PaymentPage() {
  const { bookingId } = useParams();
  const navigate = useNavigate();

  const [booking, setBooking]     = useState(null);
  const [loading, setLoading]     = useState(true);
  const [method, setMethod]       = useState('UPI');
  const [upiId, setUpiId]         = useState('');
  const [cardNum, setCardNum]     = useState('');
  const [bankName, setBankName]   = useState('');
  const [expiry, setExpiry]       = useState('');
  const [cvv, setCvv]             = useState('');
  const [paying, setPaying]       = useState(false);
  const [success, setSuccess]     = useState(false);
  const [ticketId, setTicketId]   = useState(null);
  const [error, setError]         = useState('');
  const [secondsLeft, setSecondsLeft] = useState(0);

  useEffect(() => { fetchBooking(); }, [bookingId]);

  useEffect(() => {
    if (!booking?.holdExpiresAt) return;
    const updateTimer = () => {
      const remain = Math.max(0, Math.floor((new Date(booking.holdExpiresAt).getTime() - Date.now()) / 1000));
      setSecondsLeft(remain);
    };
    updateTimer();
    const t = setInterval(updateTimer, 1000);
    return () => clearInterval(t);
  }, [booking]);

  const fetchBooking = async () => {
    try {
      const res = await api.get(`/api/bookings/${bookingId}`);
      setBooking(res.data.data);
    } catch { setError('Booking not found.'); }
    finally { setLoading(false); }
  };

  const handlePay = async () => {
    setError('');
    if (method === 'UPI' && !upiId.includes('@')) { setError('Enter valid UPI ID (e.g., name@upi)'); return; }
    if (method === 'CARD' && cardNum.replace(/\s/g,'').length < 16) { setError('Enter valid 16-digit card number'); return; }
    if (method === 'NET_BANKING' && !bankName.trim()) { setError('Select a bank for net banking'); return; }
    if (secondsLeft === 0) { setError('Payment window expired. Please rebook seats.'); return; }

    setPaying(true);
    try {
      const res = await api.post('/api/payments/process', {
        bookingId,
        paymentMethod: method,
        upiId: method === 'UPI' ? upiId : null,
        cardNumber: method === 'CARD' ? cardNum.replace(/\s/g,'') : null,
        bankName: method === 'NET_BANKING' ? bankName : null,
      });
      const tid = res.data.data?.ticket?.id;
      setTicketId(tid);
      setSuccess(true);
      setTimeout(() => navigate(`/ticket/${tid}`), 2500);
    } catch (err) {
      setError(err.response?.data?.message || 'Payment failed. Try again.');
    } finally { setPaying(false); }
  };

  const formatCard = (val) => {
    const clean = val.replace(/\D/g,'').slice(0,16);
    return clean.replace(/(.{4})/g,'$1 ').trim();
  };

  if (loading) return (
    <div style={{ minHeight:'100vh', background:'#07070e', display:'flex', alignItems:'center', justifyContent:'center' }}>
      <div style={spinner}/>
    </div>
  );

  // ── SUCCESS SCREEN ──
  if (success) return (
    <div style={{
      minHeight:'100vh', background:'#07070e', display:'flex',
      flexDirection:'column', alignItems:'center', justifyContent:'center',
      fontFamily:"'Segoe UI',sans-serif",
    }}>
      <div style={{ textAlign:'center', animation:'fadeUp 0.6s ease' }}>
        <div style={{ fontSize:'80px', marginBottom:'16px', animation:'pulse 0.8s ease infinite' }}>🎉</div>
        <h2 style={{ color:'#00ff88', fontSize:'28px', fontWeight:'900', marginBottom:'8px' }}>
          Payment Successful!
        </h2>
        <p style={{ color:'#667799', marginBottom:'4px' }}>
          Your ticket is being generated...
        </p>
        <div style={{ color:'#00d4ff', fontSize:'14px' }}>Redirecting to your ticket ✨</div>
        <div style={{ marginTop:'24px' }}>
          <div style={{ ...spinner, borderTop:'3px solid #00ff88' }} />
        </div>
      </div>
      <style>{`
        @keyframes fadeUp { from{opacity:0;transform:translateY(20px)} to{opacity:1;transform:translateY(0)} }
        @keyframes pulse  { 0%,100%{transform:scale(1)} 50%{transform:scale(1.1)} }
        @keyframes spin   { to{transform:rotate(360deg)} }
      `}</style>
    </div>
  );

  const methods = [
    { id:'UPI',    label:'UPI',         icon:'📱', color:'#00cc88', desc:'Pay instantly via UPI' },
    { id:'CARD',   label:'Card',         icon:'💳', color:'#4488ff', desc:'Credit / Debit Card' },
    { id:'WALLET', label:'CineWallet',   icon:'👛', color:'#ff8800', desc:'Balance: ₹500' },
    { id:'NET_BANKING', label:'Net Banking', icon:'🏦', color:'#00b5a0', desc:'Pay using bank account' },
  ];

  return (
    <div style={{ minHeight:'100vh', background:'#07070e', fontFamily:"'Segoe UI',sans-serif" }}>
      <Navbar theme="user"/>
      <div style={{ maxWidth:'700px', margin:'0 auto', padding:'32px 24px' }}>
        <button onClick={() => navigate(-1)} style={{ background:'none', border:'none', color:'#667799', cursor:'pointer', marginBottom:'20px', fontSize:'14px' }}>
          ← Back
        </button>

        <h1 style={{ color:'#fff', fontSize:'24px', fontWeight:'900', margin:'0 0 24px' }}>
          💳 Complete Payment
        </h1>

        {/* Booking Summary */}
        {booking && (
          <div style={{
            background:'rgba(0,212,255,0.05)', border:'1px solid #00d4ff22',
            borderRadius:'16px', padding:'20px', marginBottom:'24px',
          }}>
            <h3 style={{ color:'#00d4ff', margin:'0 0 12px', fontSize:'14px', textTransform:'uppercase', letterSpacing:'1px' }}>
              Booking Summary
            </h3>
            <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:'10px' }}>
              {[
                ['🎫 Booking', booking.id?.slice(-8).toUpperCase()],
                ['💺 Seats', booking.seatNumbers?.join(', ')],
                ['📊 Count', `${booking.numberOfSeats} seat(s)`],
                ['📋 Status', booking.status],
              ].map(([k,v]) => (
                <div key={k}>
                  <div style={{ color:'#667799', fontSize:'11px' }}>{k}</div>
                  <div style={{ color:'#fff', fontWeight:'700', fontSize:'14px' }}>{v}</div>
                </div>
              ))}
            </div>
            <div style={{ borderTop:'1px solid #ffffff11', marginTop:'12px', paddingTop:'12px', display:'flex', justifyContent:'space-between', alignItems:'center' }}>
              <span style={{ color:'#667799' }}>Total Amount</span>
              <span style={{ color:'#00d4ff', fontSize:'24px', fontWeight:'900' }}>₹{booking.totalAmount}</span>
            </div>
          </div>
        )}

        {/* Payment Method Cards */}
        <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:'12px', marginBottom:'24px' }}>
          {methods.map(m => (
            <div key={m.id} onClick={() => setMethod(m.id)} style={{
              padding:'16px 12px', borderRadius:'14px', textAlign:'center',
              cursor:'pointer', transition:'all 0.2s',
              background: method===m.id ? `${m.color}20` : 'rgba(255,255,255,0.03)',
              border: `2px solid ${method===m.id ? m.color : '#ffffff11'}`,
              boxShadow: method===m.id ? `0 0 20px ${m.color}33` : 'none',
            }}>
              <div style={{ fontSize:'28px', marginBottom:'6px' }}>{m.icon}</div>
              <div style={{ color: method===m.id ? m.color : '#fff', fontWeight:'800', fontSize:'14px' }}>{m.label}</div>
              <div style={{ color:'#667799', fontSize:'11px', marginTop:'2px' }}>{m.desc}</div>
            </div>
          ))}
        </div>

        {/* Method-specific inputs */}
        <div style={{ background:'rgba(255,255,255,0.03)', borderRadius:'16px', padding:'20px', marginBottom:'20px' }}>
          {method === 'UPI' && (
            <div>
              <label style={{ color:'#667799', fontSize:'13px', display:'block', marginBottom:'8px' }}>UPI ID</label>
              <input placeholder="yourname@upi" value={upiId} onChange={e=>setUpiId(e.target.value)} style={inputStyle}/>
              <p style={{ color:'#667799', fontSize:'12px', marginTop:'8px' }}>e.g., mobile@paytm · name@gpay · id@phonepe</p>
            </div>
          )}
          {method === 'CARD' && (
            <div style={{ display:'flex', flexDirection:'column', gap:'12px' }}>
              <div>
                <label style={{ color:'#667799', fontSize:'13px', display:'block', marginBottom:'6px' }}>Card Number</label>
                <input placeholder="1234 5678 9012 3456" value={cardNum}
                  onChange={e=>setCardNum(formatCard(e.target.value))} maxLength={19} style={inputStyle}/>
              </div>
              <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:'12px' }}>
                <div>
                  <label style={{ color:'#667799', fontSize:'13px', display:'block', marginBottom:'6px' }}>Expiry</label>
                  <input placeholder="MM/YY" value={expiry} onChange={e=>setExpiry(e.target.value)} maxLength={5} style={inputStyle}/>
                </div>
                <div>
                  <label style={{ color:'#667799', fontSize:'13px', display:'block', marginBottom:'6px' }}>CVV</label>
                  <input placeholder="•••" value={cvv} onChange={e=>setCvv(e.target.value)} maxLength={3} type="password" style={inputStyle}/>
                </div>
              </div>
            </div>
          )}
          {method === 'WALLET' && (
            <div style={{ textAlign:'center', padding:'12px 0' }}>
              <div style={{ fontSize:'36px', marginBottom:'8px' }}>👛</div>
              <div style={{ color:'#fff', fontWeight:'700' }}>CineBook Wallet Balance</div>
              <div style={{ color:'#ff8800', fontSize:'24px', fontWeight:'900', marginTop:'4px' }}>₹500.00</div>
              <div style={{ color:'#667799', fontSize:'13px', marginTop:'8px' }}>
                {booking?.totalAmount <= 500
                  ? '✅ Sufficient balance'
                  : '❌ Insufficient balance — please use another method'}
              </div>
            </div>
          )}
          {method === 'NET_BANKING' && (
            <div>
              <label style={{ color:'#667799', fontSize:'13px', display:'block', marginBottom:'8px' }}>Bank</label>
              <select value={bankName} onChange={e => setBankName(e.target.value)} style={inputStyle}>
                <option value="">Select your bank</option>
                <option value="SBI">State Bank of India</option>
                <option value="HDFC">HDFC Bank</option>
                <option value="ICICI">ICICI Bank</option>
                <option value="AXIS">Axis Bank</option>
              </select>
            </div>
          )}
        </div>

        <div style={{ marginBottom:'12px', color: secondsLeft > 60 ? '#00ff88' : '#ff9900', fontSize:'13px', fontWeight:'700' }}>
          ⏱ Payment hold expires in: {String(Math.floor(secondsLeft / 60)).padStart(2, '0')}:{String(secondsLeft % 60).padStart(2, '0')}
        </div>

        {error && (
          <div style={{ background:'#ff446620', border:'1px solid #ff4466', color:'#ff4466',
            padding:'12px 16px', borderRadius:'10px', marginBottom:'16px', fontSize:'14px' }}>
            ⚠️ {error}
          </div>
        )}

        <button onClick={handlePay} disabled={paying || secondsLeft === 0} style={{
          width:'100%', padding:'16px', borderRadius:'14px', border:'none',
          background: (paying || secondsLeft === 0) ? '#1a3a3a' : 'linear-gradient(135deg,#00d4ff,#0066ff)',
          color:'#fff', fontWeight:'900', fontSize:'16px',
          cursor: (paying || secondsLeft === 0) ? 'not-allowed' : 'pointer',
          boxShadow: (paying || secondsLeft === 0) ? 'none' : '0 8px 30px #00d4ff44',
          transition:'all 0.2s',
        }}>
          {secondsLeft === 0 ? 'Payment Expired' : paying ? '⏳ Processing Payment...' : `💰 Pay ₹${booking?.totalAmount || '—'}`}
        </button>

        <p style={{ color:'#445566', fontSize:'12px', textAlign:'center', marginTop:'12px' }}>
          🔒 Secured by CineBook · 256-bit SSL Encryption
        </p>
      </div>
      <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
    </div>
  );
}

const inputStyle = {
  width:'100%', padding:'12px 16px', borderRadius:'10px',
  background:'rgba(255,255,255,0.06)', border:'1px solid #ffffff22',
  color:'#fff', fontSize:'15px', outline:'none', boxSizing:'border-box',
};

const spinner = {
  width:'40px', height:'40px', margin:'0 auto',
  border:'3px solid #ffffff11', borderTop:'3px solid #00d4ff',
  borderRadius:'50%', animation:'spin 0.8s linear infinite',
};
