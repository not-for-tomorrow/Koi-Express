import VNPayLogo from "../../assets/images/LogoPayments/VNPay.png";
import Cashbysender from "../../assets/images/LogoPayments/Cashbysender.png";
import Cashbyrep from "../../assets/images/LogoPayments/Cashbyrep.png";

// Array of non-cash payment methods
export const nonCashPaymentMethods = [
  { label: "VNPAY", icon: VNPayLogo },
];

// Array of cash payment methods
export const cashPaymentMethods = [
  { label: "Người gửi trả tiền", icon: Cashbysender },
  { label: "Người nhận trả tiền", icon: Cashbyrep },
];


// Helper function to get the icon by payment method label
export const getPaymentMethodIcon = (methodLabel) => {
  const method = paymentMethods.find((m) => m.label === methodLabel);
  return method ? method.icon : null;
};
