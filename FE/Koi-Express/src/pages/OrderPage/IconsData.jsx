import VNPayLogo from "../../assets/images/LogoPayments/VNPay.png";
import KoiBalanceLogo from "../../assets/images/LogoPayments/KoiBalance.png";
import CashByRepIcon from "../../assets/images/LogoPayments/Cashbyrep.png";
import CashBySendIcon from "../../assets/images/LogoPayments/Cashbysender.png";

// Array of payment methods with labels and icons
export const paymentMethods = [
  { label: "Tài Khoản", icon: KoiBalanceLogo },
  { label: "VNPay", icon: VNPayLogo },
];

// Array of cash payment methods
export const cashPaymentMethods = [
  { label: "Người gửi trả tiền mặt", icon: CashBySendIcon },
  { label: "Người nhận trả tiền mặt", icon: CashByRepIcon },
];
// Helper function to get the icon by payment method label
export const getPaymentMethodIcon = (methodLabel) => {
  const method =
    paymentMethods.find((m) => m.label === methodLabel) ||
    cashPaymentMethods.find((m) => m.label === methodLabel); // Check both arrays
  return method ? method.icon : null;
};
