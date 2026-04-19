import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';
import Toast from '../components/Toast';

/**
 * BookingHistory — User's complete booking list with cancel + ticket view
 * Calls: GET /api/bookings/my → list of bookings
 *        POST /api/bookings/{id}/cancel → cancel booking
 *        GET /api/bookings/{id}/ticket → view ticket
 */
const BookingHistory = () => {
  const [bookings, setBookings]       = useState([]);
  const [loading, setLoading]         = useState(true);
  const [cancelling, setCancelling]   = useState(null);
  const [cancelDialog, setCancelDialog] = useState({ open: false, bookingId: null, preview: null });
  const [toast, setToast]             = useState(null);
  const navigate = useNavigate();

  useEffect(() => { fetchBookings(); }, []);

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/bookings/my');
      setBookings((res.data.data || []).reverse());
    } catch {
      setToast({ message: 'Failed to load bookings', type: 'error' });
    } finally { setLoading(false); }
  };

  const handleCancel = async (bookingId) => {
    try {
      const previewRes = await api.get(`/api/bookings/${bookingId}/cancellation-preview`);
      const preview = previewRes.data?.data;
      if (!preview?.cancellable) {
        setToast({ message: preview?.reason || 'Cancellation is not allowed.', type: 'error' });
        return;
      }

      setCancelDialog({ open: true, bookingId, preview });
    } catch (err) {
      setToast({ message: err.response?.data?.message || 'Cancel failed', type: 'error' });
    }
  };

  const confirmCancellation = async () => {
    if (!cancelDialog.bookingId) return;
    try {
      setCancelling(cancelDialog.bookingId);
      await api.post(`/api/bookings/${cancelDialog.bookingId}/cancel`);
      setToast({ message: 'Booking cancelled. Refund initiated.', type: 'success' });
      setCancelDialog({ open: false, bookingId: null, preview: null });
      fetchBookings();
    } catch (err) {
      setToast({ message: err.response?.data?.message || 'Cancel failed', type: 'error' });
    } finally {
      setCancelling(null);
    }
  };

  const closeCancelDialog = () => {
    if (cancelling) return;
    setCancelDialog({ open: false, bookingId: null, preview: null });
  };

  const viewTicket = async (bookingId) => {
    try {
      const res = await api.get(`/api/bookings/${bookingId}/ticket`);
      if (res.data.data?.id) navigate(`/ticket/${res.data.data.id}`);
    } catch {
      setToast({ message: 'Ticket not found for this booking', type: 'error' });
    }
  };

  const statusColor = (s) =>
    s === 'CONFIRMED' ? '#00ff88' : s === 'CANCELLED' ? '#ff4466' : '#ffaa00';

  const statusIcon = (s) =>
    s === 'CONFIRMED' ? '✅' : s === 'CANCELLED' ? '❌' : '⏳';

  return (
    <div style={{ minHeight: '100vh', background: '#07070e', fontFamily: "'Segoe UI',sans-serif" }}>
      <Navbar theme="user" />

      <div style={{ maxWidth: '900px', margin: '0 auto', padding: '32px 24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '28px' }}>
          <div>
            <h1 style={{ color: '#fff', fontSize: '26px', fontWeight: '900', margin: 0 }}>📋 My Bookings</h1>
            <p style={{ color: '#667799', fontSize: '14px', margin: '4px 0 0' }}>
              Your complete booking history
            </p>
          </div>
          <button onClick={fetchBookings} style={{
            background: 'rgba(0,212,255,0.08)', border: '1px solid #00d4ff44',
            color: '#00d4ff', padding: '8px 18px', borderRadius: '10px',
            cursor: 'pointer', fontSize: '13px', fontWeight: '600',
          }}>🔄 Refresh</button>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '80px 0', color: '#667799' }}>
            <div style={spinnerStyle} />
            <p style={{ marginTop: '16px' }}>Loading your bookings...</p>
          </div>
        ) : bookings.length === 0 ? (
          <div style={{
            textAlign: 'center', padding: '80px 0',
            background: 'rgba(255,255,255,0.02)', borderRadius: '20px',
            border: '1px solid #ffffff11',
          }}>
            <div style={{ fontSize: '64px', marginBottom: '16px' }}>🎭</div>
            <h3 style={{ color: '#fff', marginBottom: '8px' }}>No bookings yet</h3>
            <p style={{ color: '#667799', marginBottom: '24px' }}>Book your first movie ticket!</p>
            <Link to="/movies" style={{
              padding: '12px 28px', background: 'linear-gradient(135deg, #00d4ff, #0066ff)',
              borderRadius: '12px', color: '#fff', textDecoration: 'none',
              fontWeight: '700', boxShadow: '0 0 20px #00d4ff44',
            }}>Browse Movies →</Link>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {bookings.map(booking => (
              <div key={booking.id} style={{
                background: 'rgba(255,255,255,0.03)',
                border: `1px solid ${booking.status === 'CONFIRMED' ? '#00d4ff22' : booking.status === 'CANCELLED' ? '#ff446622' : '#ffaa0022'}`,
                borderRadius: '16px', padding: '20px 24px', position: 'relative',
                transition: 'transform 0.2s',
              }}>
                {/* Status badge */}
                <div style={{
                  position: 'absolute', top: '16px', right: '16px',
                  background: `${statusColor(booking.status)}15`,
                  border: `1px solid ${statusColor(booking.status)}44`,
                  color: statusColor(booking.status),
                  fontSize: '12px', fontWeight: '800', padding: '4px 12px',
                  borderRadius: '20px', letterSpacing: '1px',
                }}>
                  {statusIcon(booking.status)} {booking.status}
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px', marginBottom: '16px' }}>
                  <div>
                    <div style={{ color: '#667799', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '4px' }}>🎫 Booking ID</div>
                    <div style={{ color: '#fff', fontFamily: 'monospace', fontSize: '13px', fontWeight: '700' }}>
                      {booking.id?.slice(-10).toUpperCase()}
                    </div>
                  </div>
                  <div>
                    <div style={{ color: '#667799', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '4px' }}>💺 Seats</div>
                    <div style={{ color: '#ffdd00', fontWeight: '700', fontSize: '14px' }}>
                      {booking.seatNumbers?.join(', ') || 'N/A'}
                    </div>
                  </div>
                  <div>
                    <div style={{ color: '#667799', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '4px' }}>💰 Amount</div>
                    <div style={{ color: '#00d4ff', fontWeight: '900', fontSize: '18px' }}>₹{booking.totalAmount}</div>
                  </div>
                  <div>
                    <div style={{ color: '#667799', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '4px' }}>🎭 Seats Count</div>
                    <div style={{ color: '#fff', fontWeight: '700' }}>{booking.numberOfSeats} seat(s)</div>
                  </div>
                  <div>
                    <div style={{ color: '#667799', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '4px' }}>📅 Booked On</div>
                    <div style={{ color: '#fff', fontSize: '13px' }}>
                      {booking.bookingTime ? new Date(booking.bookingTime).toLocaleDateString('en-IN', {
                        day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
                      }) : 'N/A'}
                    </div>
                  </div>
                  <div>
                    <div style={{ color: '#667799', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '4px' }}>💳 Payment</div>
                    <div style={{ color: '#fff', fontSize: '13px' }}>{booking.paymentId ? 'Paid' : 'Pending'}</div>
                  </div>
                </div>

                {/* Action buttons */}
                <div style={{ display: 'flex', gap: '10px', borderTop: '1px solid #ffffff0a', paddingTop: '14px' }}>
                  {booking.status === 'CONFIRMED' && (
                    <button onClick={() => viewTicket(booking.id)} style={{
                      padding: '8px 20px', background: 'linear-gradient(135deg, #00d4ff, #0066ff)',
                      border: 'none', borderRadius: '8px', color: '#fff',
                      cursor: 'pointer', fontSize: '13px', fontWeight: '700',
                      boxShadow: '0 0 15px #00d4ff33',
                    }}>🎟️ View Ticket</button>
                  )}
                  {booking.status === 'PENDING' && (
                    <button onClick={() => navigate(`/payment/${booking.id}`)} style={{
                      padding: '8px 20px', background: 'linear-gradient(135deg, #ffaa00, #ff6600)',
                      border: 'none', borderRadius: '8px', color: '#fff',
                      cursor: 'pointer', fontSize: '13px', fontWeight: '700',
                    }}>⚡ Complete Payment</button>
                  )}
                  {['CONFIRMED', 'PENDING'].includes(booking.status) && (
                    <button
                      onClick={() => handleCancel(booking.id)}
                      disabled={cancelling === booking.id}
                      style={{
                        padding: '8px 20px',
                        background: cancelling === booking.id ? '#1a1a1a' : 'transparent',
                        border: '1px solid #ff446644',
                        borderRadius: '8px', color: cancelling === booking.id ? '#555' : '#ff4466',
                        cursor: cancelling === booking.id ? 'not-allowed' : 'pointer',
                        fontSize: '13px', fontWeight: '600',
                        transition: 'all 0.2s',
                      }}>
                      {cancelling === booking.id ? '⏳ Cancelling...' : '✕ Cancel'}
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {cancelDialog.open && cancelDialog.preview && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 1100,
          background: 'rgba(0,0,0,0.72)', display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: '20px',
        }}>
          <div style={{
            width: 'min(520px, 100%)',
            background: 'linear-gradient(180deg, #11192a 0%, #0b111d 100%)',
            border: '1px solid #00d4ff33', borderRadius: '16px',
            boxShadow: '0 20px 60px rgba(0,0,0,0.45)',
            overflow: 'hidden',
          }}>
            <div style={{
              padding: '16px 18px', borderBottom: '1px solid #ffffff12',
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
            }}>
              <div style={{ color: '#fff', fontWeight: '800', fontSize: '17px' }}>
                Confirm Cancellation
              </div>
              <button onClick={closeCancelDialog} disabled={!!cancelling} style={{
                background: 'transparent', border: 'none', color: '#89a0c2',
                fontSize: '20px', cursor: cancelling ? 'not-allowed' : 'pointer',
              }}>×</button>
            </div>

            <div style={{ padding: '18px' }}>
              <div style={{
                background: '#0f2135', border: '1px solid #00d4ff2f', borderRadius: '12px',
                padding: '14px', marginBottom: '14px',
              }}>
                <div style={{ color: '#8fb0d8', fontSize: '12px', letterSpacing: '0.7px', textTransform: 'uppercase' }}>
                  Refund Policy
                </div>
                <div style={{ color: '#fff', fontWeight: '700', fontSize: '20px', marginTop: '4px' }}>
                  {cancelDialog.preview.refundPercent}%
                </div>
              </div>

              <div style={{
                display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px',
                marginBottom: '14px',
              }}>
                <div style={{ background: '#121c2b', border: '1px solid #ffffff14', borderRadius: '10px', padding: '10px' }}>
                  <div style={{ color: '#8ea1bd', fontSize: '12px' }}>Booking</div>
                  <div style={{ color: '#fff', fontWeight: '700', marginTop: '4px' }}>
                    {cancelDialog.bookingId?.slice(-10).toUpperCase()}
                  </div>
                </div>
                <div style={{ background: '#121c2b', border: '1px solid #ffffff14', borderRadius: '10px', padding: '10px' }}>
                  <div style={{ color: '#8ea1bd', fontSize: '12px' }}>Refund Amount</div>
                  <div style={{ color: '#00e1a5', fontWeight: '900', marginTop: '4px', fontSize: '18px' }}>
                    Rs {cancelDialog.preview.refundAmount.toFixed(2)}
                  </div>
                </div>
              </div>

              <div style={{
                background: '#1a2232', border: '1px solid #ffffff14', borderRadius: '10px',
                padding: '12px', color: '#ced8e8', fontSize: '14px', lineHeight: '1.45',
              }}>
                {cancelDialog.preview.reason}
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '18px' }}>
                <button onClick={closeCancelDialog} disabled={!!cancelling} style={{
                  border: '1px solid #ffffff2a', background: 'transparent', color: '#d4def0',
                  padding: '9px 16px', borderRadius: '9px', cursor: cancelling ? 'not-allowed' : 'pointer',
                  fontWeight: '600',
                }}>
                  Keep Booking
                </button>
                <button onClick={confirmCancellation} disabled={!!cancelling} style={{
                  border: 'none', background: 'linear-gradient(135deg, #ff4f7a, #ff3e5f)', color: '#fff',
                  padding: '10px 18px', borderRadius: '9px', cursor: cancelling ? 'not-allowed' : 'pointer',
                  fontWeight: '800', boxShadow: '0 8px 18px rgba(255,79,122,0.35)',
                }}>
                  {cancelling ? 'Cancelling...' : 'Confirm Cancellation'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
};

const spinnerStyle = {
  width: '36px', height: '36px', margin: '0 auto',
  border: '3px solid #ffffff11', borderTop: '3px solid #00d4ff',
  borderRadius: '50%', animation: 'spin 0.8s linear infinite',
};

export default BookingHistory;
