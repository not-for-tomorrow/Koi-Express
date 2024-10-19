import VNPayLogo from "../../assets/images/LogoPayments/VNPay.png";

// Array of payment methods with labels and icons
export const paymentMethods = [
  { label: "VNPAY", icon: VNPayLogo },
];

// Helper function to get the icon by payment method label
export const getPaymentMethodIcon = (methodLabel) => {
  const method = paymentMethods.find((m) => m.label === methodLabel);
  return method ? method.icon : null;
};
