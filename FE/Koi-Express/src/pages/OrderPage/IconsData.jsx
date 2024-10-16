import ZaloPayIcon from "../../assets/images/IconsPayment/Zalo.png";

// Array of payment methods with labels and icons
export const paymentMethods = [
  { label: "Tài khoản" }, // No icon needed here
  { label: "ZaloPay", icon: ZaloPayIcon },
  { label: "ViettelPay",},
  { label: "Ví MoMo",},
  { label: "ShopeePay", },
];

// Array of cash payment methods
export const cashPaymentMethods = [
  { label: "Người gửi trả tiền mặt" },
  { label: "Người nhận trả tiền mặt" },
];

// Helper function to get the icon by payment method label
export const getPaymentMethodIcon = (methodLabel) => {
  const method = paymentMethods.find((m) => m.label === methodLabel);
  return method ? method.icon : null;
};
