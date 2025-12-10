import React from 'react';

interface StatsCardProps {
  title: string;
  value: number;
  change?: string;
  icon: React.ReactNode;
  bgColor: string;
  iconColor: string;
}

export const StatsCard: React.FC<StatsCardProps> = ({
  title,
  value,
  change,
  icon,
  bgColor,
  iconColor,
}) => {
  return (
    <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-600 mb-2">{title}</p>
          <p className="text-3xl font-bold text-gray-900">{value}</p>
          {change && (
            <p className="text-sm text-emerald-600 mt-2 font-medium">{change}</p>
          )}
        </div>
        <div className={`${bgColor} ${iconColor} p-3 rounded-lg`}>
          {icon}
        </div>
      </div>
    </div>
  );
};
