import React from "react";
import Link from "next/link";
import { ArrowRight } from "lucide-react";

interface QuickActionCardProps {
  title: string;
  description: string;
  icon: React.ReactNode;
  bgColor: string;
  textColor?: string;
  href: string;
}

export const QuickActionCard: React.FC<QuickActionCardProps> = ({
  title,
  description,
  icon,
  bgColor,
  textColor = "text-white",
  href,
}) => {
  return (
    <Link href={href}>
      <div
        className={`${bgColor} ${textColor} rounded-xl p-6 hover:opacity-90 transition-opacity cursor-pointer group relative overflow-hidden`}
      >
        <div className="flex items-start justify-between mb-4">
          <div
            className={`${
              textColor === "text-white" ? "bg-white/20" : "bg-gray-200"
            } p-3 rounded-lg`}
          >
            {icon}
          </div>
          <ArrowRight className="w-5 h-5 opacity-0 group-hover:opacity-100 transition-opacity" />
        </div>
        <h3 className="text-lg font-semibold mb-2">{title}</h3>
        <p
          className={`text-sm ${
            textColor === "text-white" ? "text-white/80" : "text-gray-600"
          }`}
        >
          {description}
        </p>
      </div>
    </Link>
  );
};
